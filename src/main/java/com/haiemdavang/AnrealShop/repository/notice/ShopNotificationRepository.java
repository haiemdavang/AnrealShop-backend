package com.haiemdavang.AnrealShop.repository.notice;

import com.haiemdavang.AnrealShop.modal.entity.notification.ShopNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopNotificationRepository extends JpaRepository<ShopNotification, String> {
    Page<ShopNotification> findByShopIdOrderByCreatedAtDesc(String shopId, Pageable pageable);
    long countByShopIdAndIsReadFalse(String shopId);

    @Modifying
    @Query("UPDATE ShopNotification s SET s.isRead = true WHERE s.shop.id = :shopId AND s.isRead = false")
    void markAllAsReadByShopId(@Param("shopId") String shopId);
}
