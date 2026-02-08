package com.haiemdavang.AnrealShop.repository.chat;

import com.haiemdavang.AnrealShop.modal.entity.chat.ChatMessage;
import com.haiemdavang.AnrealShop.modal.enums.SenderRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    Page<ChatMessage> findByRoomIdOrderByCreatedAtDesc(String roomId, Pageable pageable);

    // Lấy tin nhắn cuối cùng của phòng chat
    Optional<ChatMessage> findTopByRoomIdOrderByCreatedAtDesc(String roomId);

    // Đếm số tin nhắn chưa đọc trong phòng chat (tin nhắn không phải của senderRole hiện tại và chưa đọc)
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.room.id = :roomId AND cm.senderRole != :senderRole AND cm.isRead = false")
    int countUnreadMessages(@Param("roomId") String roomId, @Param("senderRole") SenderRole senderRole);

    // Đánh dấu tất cả tin nhắn trong phòng là đã đọc (trừ tin nhắn của chính mình - theo senderRole)
    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.isRead = true WHERE cm.room.id = :roomId AND cm.senderRole != :senderRole AND cm.isRead = false")
    void markAllAsRead(@Param("roomId") String roomId, @Param("senderRole") SenderRole senderRole);
}
