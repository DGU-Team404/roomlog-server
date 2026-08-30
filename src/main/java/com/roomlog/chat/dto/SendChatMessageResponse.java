package com.roomlog.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.chat.domain.AppGuide;
import com.roomlog.chat.domain.ChatMessage;
import lombok.Getter;

import java.util.List;

@Getter
public class SendChatMessageResponse {

    /** 답변을 만든 방식. GUIDE(고정 안내) · CACHE(캐시 재사용) · GPT(신규 생성) · FALLBACK(안내 범위 밖) */
    public enum Source { GUIDE, CACHE, GPT, FALLBACK }

    @JsonProperty("message_id")
    private final Long messageId;

    private final String answer;

    private final Source source;

    @JsonProperty("suggested_questions")
    private final List<SuggestedQuestion> suggestedQuestions;

    private SendChatMessageResponse(ChatMessage message, Source source) {
        this.messageId = message.getId();
        this.answer = message.getContent();
        this.source = source;
        this.suggestedQuestions = source == Source.FALLBACK
                ? AppGuide.suggested().stream().map(SuggestedQuestion::from).toList()
                : null;
    }

    public static SendChatMessageResponse of(ChatMessage message, Source source) {
        return new SendChatMessageResponse(message, source);
    }
}
