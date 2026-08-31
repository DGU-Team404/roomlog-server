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

    /** 하자를 골라 주고받은 메시지면 그 하자 ID. 직접 입력한 질문이면 null. */
    @JsonProperty("defect_id")
    private final Long defectId;

    @JsonProperty("created_at")
    private final LocalDateTime createdAt;

    private ChatMessageItem(ChatMessage message) {
        this.messageId = message.getId();
        this.role = message.getRole().name();
        this.content = message.getContent();
        this.defectId = message.getDefectId();
        this.createdAt = message.getCreatedAt();
    }

    public static ChatMessageItem from(ChatMessage message) {
        return new ChatMessageItem(message);
    }
}
