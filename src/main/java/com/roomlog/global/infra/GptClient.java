package com.roomlog.global.infra;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roomlog.global.exception.CustomException;
import com.roomlog.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GptClient {

    private static final String CHAT_COMPLETIONS_URL = "https://api.openai.com/v1/chat/completions";

    private static final int CHAT_MAX_TOKENS = 500;

    private static final String SYSTEM_PROMPT = """
            너는 한국의 원룸/자취방 하자 보수 전문가다. 사용자가 하자 정보를 주면 자가 수리 안내문을 만든다.

            [매우 중요]
            - 자가 수리 가능 여부는 이미 결정되어 전달된다. 그 판정을 절대 뒤집지 말고 그대로 따른다.
            - URL은 절대 생성하지 마라. 링크는 서버가 검색어로 직접 만든다. 검색어(한국어)만 반환하라.

            [description 작성 규칙]
            - 반드시 "해당 하자는 ~한 하자로, ~하기에 스스로 수리 가능합니다." 또는
              "해당 하자는 ~한 하자로, ~하기에 스스로 수리 불가능합니다." 형식의 한 문장으로 쓴다.
            - 하자 종류와 이유가 구체적으로 드러나게 쓴다.

            [자가 수리 가능한 경우]
            - video_search_query: 유튜브에서 이 하자의 셀프 보수 영상을 찾을 검색어 (예: "벽지 들뜸 셀프 보수").

            [자가 수리 불가능한 경우]
            - video_search_query: 빈 문자열.
            """;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gpt.api-key}")
    private String apiKey;

    @Value("${gpt.model}")
    private String model;

    public SelfRepairGuide generateSelfRepairGuide(String type, String severity, String location,
                                                  Float area, String defectDescription, boolean selfRepairPossible) {
        String userPrompt = """
                하자 종류: %s
                심각도: %s
                위치: %s
                면적: %s㎡
                탐지 설명: %s
                자가 수리 가능 여부(확정): %s
                """.formatted(
                type,
                severity,
                location,
                area != null ? area : "미상",
                defectDescription != null && !defectDescription.isBlank() ? defectDescription : "없음",
                selfRepairPossible ? "가능" : "불가능");

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", userPrompt)),
                "response_format", Map.of(
                        "type", "json_schema",
                        "json_schema", Map.of(
                                "name", "self_repair_guide",
                                "strict", true,
                                "schema", responseSchema())));

        try {
            ChatCompletionResponse response = restTemplate.postForObject(
                    CHAT_COMPLETIONS_URL, authEntity(body), ChatCompletionResponse.class);

            String content = response.getChoices().get(0).getMessage().getContent();
            return objectMapper.readValue(content, SelfRepairGuide.class);
        } catch (Exception e) {
            log.error("GPT self-repair guide error", e);
            throw new CustomException(ErrorCode.DEFECT_002);
        }
    }

    private static final String CHAT_SYSTEM_PROMPT = """
            너는 '룸로그' 앱의 사용법 안내 도우미다.
            - 사용자 질문에 [앱 안내]에 적힌 내용만 근거로 답한다. 적혀 있지 않은 기능은 절대 지어내지 마라.
            - 안내에서 답을 찾을 수 없으면 "해당 내용은 안내해드리기 어려워요."라고만 답한다.
            - 존댓말로 5문장 이내, 군더더기 없이 답한다. 단계가 여러 개면 줄바꿈으로 나눠 적어도 된다.
            """;

    /** 앱 사용법 질문에 답한다. 관련 안내 섹션과 최근 대화만 받아 생성한다. */
    public String answerAppGuide(String question, String guideContext, List<Map<String, String>> recentMessages) {
        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", CHAT_SYSTEM_PROMPT));

        for (Map<String, String> message : recentMessages) {
            messages.add(Map.of("role", message.get("role"), "content", message.get("content")));
        }

        messages.add(Map.of("role", "user", "content", """
                [앱 안내]
                %s

                [질문]
                %s""".formatted(guideContext, question)));

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", messages,
                "temperature", 0.3,
                "max_tokens", CHAT_MAX_TOKENS);

        try {
            ChatCompletionResponse response = restTemplate.postForObject(
                    CHAT_COMPLETIONS_URL, authEntity(body), ChatCompletionResponse.class);

            return response.getChoices().get(0).getMessage().getContent().trim();
        } catch (Exception e) {
            log.error("GPT app guide answer error", e);
            throw new CustomException(ErrorCode.CHAT_003);
        }
    }

    private Map<String, Object> responseSchema() {
        return Map.of(
                "type", "object",
                "properties", Map.of(
                        "description", Map.of("type", "string"),
                        "video_search_query", Map.of("type", "string")),
                "required", List.of("description", "video_search_query"),
                "additionalProperties", false);
    }

    private HttpEntity<Map<String, Object>> authEntity(Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        return new HttpEntity<>(body, headers);
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SelfRepairGuide {

        private String description;

        @JsonProperty("video_search_query")
        private String videoSearchQuery;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChatCompletionResponse {
        private List<Choice> choices;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {
        private Message message;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Message {
        private String content;
    }
}
