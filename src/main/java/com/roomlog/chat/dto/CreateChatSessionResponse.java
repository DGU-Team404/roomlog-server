package com.roomlog.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.chat.domain.AppGuide;
import com.roomlog.chat.domain.ChatSession;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateChatSessionResponse {

    @JsonProperty("session_id")
    private final Long sessionId;

    private final String greeting;

    @JsonProperty("suggested_questions")
    private final List<SuggestedQuestion> suggestedQuestions;

    private CreateChatSessionResponse(ChatSession session, String greeting) {
        this.sessionId = session.getId();
        this.greeting = greeting;
        this.suggestedQuestions = AppGuide.suggested().stream().map(SuggestedQuestion::from).toList();
    }

    public static CreateChatSessionResponse of(ChatSession session, String greeting) {
        return new CreateChatSessionResponse(session, greeting);
    }
}
