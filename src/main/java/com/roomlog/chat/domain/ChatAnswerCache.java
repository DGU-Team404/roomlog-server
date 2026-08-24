package com.roomlog.chat.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 정규화된 질문 → GPT 답변 캐시. 전체 사용자가 공유하므로 같은 질문은 두 번 호출하지 않는다.
 */
@Entity
@Table(name = "chat_answer_cache")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatAnswerCache {

    @Id
    @Column(name = "question_key", length = 200)
    private String questionKey;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String answer;

    @Column(name = "hit_count", nullable = false)
    private Integer hitCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public ChatAnswerCache(String questionKey, String answer) {
        this.questionKey = questionKey;
        this.answer = answer;
        this.hitCount = 0;
    }

    public void hit() {
        this.hitCount++;
    }
}
