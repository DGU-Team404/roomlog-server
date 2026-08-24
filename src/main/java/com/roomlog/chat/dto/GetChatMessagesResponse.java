package com.roomlog.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.chat.domain.ChatMessage;
import lombok.Getter;

import java.util.List;

@Getter
public class GetChatMessagesResponse {

    @JsonProperty("session_id")
    private final Long sessionId;

    private final List<ChatMessageItem> messages;

    private GetChatMessagesResponse(Long sessionId, List<ChatMessage> messages) {
        this.sessionId = sessionId;
        this.messages = messages.stream().map(ChatMessageItem::from).toList();
    }

    public static GetChatMessagesResponse of(Long sessionId, List<ChatMessage> messages) {
        return new GetChatMessagesResponse(sessionId, messages);
    }
}
