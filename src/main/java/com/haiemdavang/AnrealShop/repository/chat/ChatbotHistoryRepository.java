package com.haiemdavang.AnrealShop.repository.chat;

import com.haiemdavang.AnrealShop.modal.entity.chat.ChatbotHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatbotHistoryRepository extends JpaRepository<ChatbotHistory, String> {

    Page<ChatbotHistory> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
}
