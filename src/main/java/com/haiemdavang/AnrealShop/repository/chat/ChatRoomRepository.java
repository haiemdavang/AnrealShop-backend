package com.haiemdavang.AnrealShop.repository.chat;

import com.haiemdavang.AnrealShop.modal.entity.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    @Modifying
    @Query("UPDATE ChatRoom cr SET cr.lastActive = :lastActive WHERE cr.id = :roomId")
    void updateLastActive(@Param("roomId") String roomId, @Param("lastActive") LocalDateTime lastActive);

    // Tìm phòng chat giữa customer và shop
    Optional<ChatRoom> findByCustomerIdAndShopId(String customerId, String shopId);

    // Lấy danh sách phòng chat của customer
    List<ChatRoom> findByCustomerIdOrderByLastActiveDesc(String customerId);

    // Lấy danh sách phòng chat của shop
    List<ChatRoom> findByShopIdOrderByLastActiveDesc(String shopId);

    // Lấy danh sách phòng chat của user (là customer hoặc là owner của shop)
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.customer.id = :userId OR cr.shop.user.id = :userId ORDER BY cr.lastActive DESC")
    List<ChatRoom> findRoomsByUserId(@Param("userId") String userId);
}
