package com.haiemdavang.AnrealShop.repository.notice;

import com.haiemdavang.AnrealShop.modal.entity.notification.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotification, String> {
    Page<UserNotification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    long countByUserIdAndIsReadFalse(String userId);

    @Modifying
    @Query("UPDATE UserNotification u SET u.isRead = true WHERE u.user.id = :userId AND u.isRead = false")
    void markAllAsReadByUserId(@Param("userId") String userId);
}
