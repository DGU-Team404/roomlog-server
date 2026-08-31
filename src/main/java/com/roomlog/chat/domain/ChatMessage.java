package com.roomlog.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message", indexes = @Index(name = "idx_chat_message_session", columnList = "chat_session_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    public enum Role { USER, ASSISTANT }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    @Column(name = "chat_session_id", nullable = false)
    private Long sessionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /** "등록된 하자에서 선택하기"로 시작된 대화인 경우 그 하자 ID. 직접 입력한 질문이면 null. */
    @Column(name = "defect_id")
    private Long defectId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public ChatMessage(Long sessionId, Role role, String content, Long defectId) {
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
        this.defectId = defectId;
    }
}
