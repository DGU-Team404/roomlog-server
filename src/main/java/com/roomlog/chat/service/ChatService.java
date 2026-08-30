package com.roomlog.chat.service;

import com.roomlog.chat.domain.AppGuide;
import com.roomlog.chat.domain.ChatAnswerCache;
import com.roomlog.chat.domain.ChatMessage;
import com.roomlog.chat.domain.ChatSession;
import com.roomlog.chat.dto.CreateChatSessionResponse;
import com.roomlog.chat.dto.GetChatMessagesResponse;
import com.roomlog.chat.dto.SendChatMessageRequest;
import com.roomlog.chat.dto.SendChatMessageResponse;
import com.roomlog.chat.repository.ChatAnswerCacheRepository;
import com.roomlog.chat.repository.ChatMessageRepository;
import com.roomlog.chat.repository.ChatSessionRepository;
import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import com.roomlog.global.infra.GptClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 앱 사용법 안내 챗봇.
 * GPT 호출은 최후 수단이며, 아래 순서로 먼저 걸러 토큰 사용을 줄인다.
 * 1) 추천 질문 버튼 → 고정 안내문 그대로 반환
 * 2) 같은 질문 캐시 적중 → 저장된 답변 재사용
 * 3) 안내 키워드에 하나도 걸리지 않음 → 안내 범위 밖 고정 답변
 * 4) 위에 해당하지 않을 때만 관련 섹션 1~2개 + 최근 대화 2턴으로 GPT 호출
 */
@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String GREETING =
            "안녕하세요! 룸로그 사용법을 안내해드릴게요. 궁금한 점을 입력하시거나 아래 질문을 눌러보세요.";
    private static final String FALLBACK_ANSWER =
            "죄송해요, 저는 룸로그 앱 사용법만 안내해드릴 수 있어요. 아래 질문 중에서 골라보시겠어요?";

    /** GPT에 실어 보낼 관련 안내 섹션 수. 늘릴수록 정확도는 오르고 토큰은 늘어난다. */
    private static final int GUIDE_CONTEXT_LIMIT = 2;
    /** GPT에 실어 보낼 직전 메시지 수(2턴). */
    private static final int HISTORY_LIMIT = 4;
    /** 직전 메시지를 잘라 보낼 길이. */
    private static final int HISTORY_CONTENT_LIMIT = 150;
    /** 캐시 키 최대 길이. ChatAnswerCache.questionKey 컬럼 길이와 맞춘다. */
    private static final int CACHE_KEY_LIMIT = 200;

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatAnswerCacheRepository chatAnswerCacheRepository;
    private final GptClient gptClient;

    @Transactional
    public CreateChatSessionResponse createSession(Long userId) {
        ChatSession session = chatSessionRepository.save(ChatSession.builder().userId(userId).build());
        return CreateChatSessionResponse.of(session, GREETING);
    }

    @Transactional
    public SendChatMessageResponse sendMessage(Long userId, Long sessionId, SendChatMessageRequest request) {
        ChatSession session = findOwnedSession(userId, sessionId);

        List<ChatMessage> history = recentHistory(session.getId());
        String question = request.getMessage().trim();

        save(session.getId(), ChatMessage.Role.USER, question);

        Answer answer = resolveAnswer(question, request.getGuide(), history);
        ChatMessage saved = save(session.getId(), ChatMessage.Role.ASSISTANT, answer.content());

        return SendChatMessageResponse.of(saved, answer.source());
    }

    @Transactional(readOnly = true)
    public GetChatMessagesResponse getMessages(Long userId, Long sessionId) {
        ChatSession session = findOwnedSession(userId, sessionId);
        return GetChatMessagesResponse.of(session.getId(),
                chatMessageRepository.findBySessionIdOrderByIdAsc(session.getId()));
    }

    private Answer resolveAnswer(String question, String guideCode, List<ChatMessage> history) {
        AppGuide selected = parseGuide(guideCode);
        if (selected != null) {
            return new Answer(selected.getContent(), SendChatMessageResponse.Source.GUIDE);
        }

        String normalized = normalize(question);
        // 이전 대화가 있으면 같은 문장이라도 의미가 달라질 수 있어 캐시를 쓰지 않는다.
        boolean cacheable = history.isEmpty() && !normalized.isBlank();

        if (cacheable) {
            ChatAnswerCache cached = chatAnswerCacheRepository.findById(normalized).orElse(null);
            if (cached != null) {
                cached.hit();
                return new Answer(cached.getAnswer(), SendChatMessageResponse.Source.CACHE);
            }
        }

        List<AppGuide> matched = AppGuide.match(normalized, previousQuestions(history), GUIDE_CONTEXT_LIMIT);
        if (matched.isEmpty()) {
            return new Answer(FALLBACK_ANSWER, SendChatMessageResponse.Source.FALLBACK);
        }

        String generated = gptClient.answerAppGuide(question, guideContext(matched), toGptMessages(history));

        if (cacheable) {
            chatAnswerCacheRepository.save(ChatAnswerCache.builder()
                    .questionKey(normalized)
                    .answer(generated)
                    .build());
        }

        return new Answer(generated, SendChatMessageResponse.Source.GPT);
    }

    /** 직전 사용자 질문을 이어붙여 정규화한다. 후속 질문의 주제를 추정하는 데 쓴다. */
    private String previousQuestions(List<ChatMessage> history) {
        return normalize(history.stream()
                .filter(message -> message.getRole() == ChatMessage.Role.USER)
                .map(ChatMessage::getContent)
                .reduce((a, b) -> a + " " + b)
                .orElse(""));
    }

    private AppGuide parseGuide(String guideCode) {
        if (guideCode == null || guideCode.isBlank()) return null;
        try {
            return AppGuide.valueOf(guideCode.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String guideContext(List<AppGuide> guides) {
        return guides.stream()
                .map(guide -> "■ " + guide.getTitle() + "\n" + guide.getContent())
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
    }

    private List<Map<String, String>> toGptMessages(List<ChatMessage> history) {
        List<Map<String, String>> messages = new ArrayList<>();
        for (ChatMessage message : history) {
            messages.add(Map.of(
                    "role", message.getRole() == ChatMessage.Role.USER ? "user" : "assistant",
                    "content", truncate(message.getContent(), HISTORY_CONTENT_LIMIT)));
        }
        return messages;
    }

    /** 최근 메시지를 오래된 순으로 반환한다. */
    private List<ChatMessage> recentHistory(Long sessionId) {
        List<ChatMessage> recent = new ArrayList<>(chatMessageRepository.findBySessionIdOrderByIdDesc(
                sessionId, PageRequest.of(0, HISTORY_LIMIT)));
        java.util.Collections.reverse(recent);
        return recent;
    }

    private ChatMessage save(Long sessionId, ChatMessage.Role role, String content) {
        return chatMessageRepository.save(ChatMessage.builder()
                .sessionId(sessionId)
                .role(role)
                .content(content)
                .build());
    }

    private ChatSession findOwnedSession(Long userId, Long sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_001));

        if (!session.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.CHAT_002);
        }
        return session;
    }

    /** 캐시 키용 정규화. 공백·문장부호를 없애 "스캔 어떻게 해요?"와 "스캔어떻게해요"를 같은 질문으로 본다. */
    private String normalize(String question) {
        String normalized = question.toLowerCase().replaceAll("[^0-9a-z가-힣]", "");
        return truncate(normalized, CACHE_KEY_LIMIT);
    }

    private String truncate(String text, int limit) {
        return text.length() <= limit ? text : text.substring(0, limit);
    }

    private record Answer(String content, SendChatMessageResponse.Source source) {
    }
}
