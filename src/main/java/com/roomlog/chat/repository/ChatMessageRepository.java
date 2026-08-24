package com.roomlog.chat.repository;

import com.roomlog.chat.domain.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findBySessionIdOrderByIdAsc(Long sessionId);

    List<ChatMessage> findBySessionIdOrderByIdDesc(Long sessionId, Pageable pageable);
}
