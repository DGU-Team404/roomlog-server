package com.roomlog.chat.repository;

import com.roomlog.chat.domain.ChatAnswerCache;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatAnswerCacheRepository extends JpaRepository<ChatAnswerCache, String> {
}
