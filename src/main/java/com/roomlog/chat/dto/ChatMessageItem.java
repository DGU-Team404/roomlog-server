package com.roomlog.chat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.roomlog.chat.domain.ChatMessage;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMessageItem {

    @JsonProperty("message_id")
    private final Long messageId;

    private final String role;

    private final String content;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    private ChatMessageItem(ChatMessage message) {
        this.messageId = message.getId();
        this.role = message.getRole().name();
        this.content = message.getContent();
        this.createdAt = message.getCreatedAt();
    }

    public static ChatMessageItem from(ChatMessage message) {
        return new ChatMessageItem(message);
    }
}
