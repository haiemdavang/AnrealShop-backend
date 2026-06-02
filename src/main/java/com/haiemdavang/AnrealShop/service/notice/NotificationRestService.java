package com.haiemdavang.AnrealShop.service.notice;

import com.haiemdavang.AnrealShop.dto.notification.NotificationResponse;
import com.haiemdavang.AnrealShop.exception.ForbiddenException;
import com.haiemdavang.AnrealShop.exception.ResourceNotFoundException;
import com.haiemdavang.AnrealShop.modal.entity.notification.ShopNotification;
import com.haiemdavang.AnrealShop.modal.entity.notification.UserNotification;
import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.repository.notice.ShopNotificationRepository;
import com.haiemdavang.AnrealShop.repository.notice.UserNotificationRepository;
import com.haiemdavang.AnrealShop.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationRestService {

    private final UserNotificationRepository userNotificationRepository;
    private final ShopNotificationRepository shopNotificationRepository;
    private final SecurityUtils securityUtils;

    public Page<NotificationResponse> getUserNotifications(Pageable pageable) {
        User user = securityUtils.getCurrentUser();
        Page<UserNotification> page = userNotificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        return page.map(NotificationResponse::fromUser);
    }

    public long getUnreadCount() {
        User user = securityUtils.getCurrentUser();
        return userNotificationRepository.countByUserIdAndIsReadFalse(user.getId());
    }

    @Transactional
    public NotificationResponse markAsRead(String id) {
        UserNotification notification = userNotificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        User user = securityUtils.getCurrentUser();
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Not allowed");
        }
        notification.setRead(true);
        userNotificationRepository.save(notification);
        return NotificationResponse.fromUser(notification);
    }

    @Transactional
    public void markAllAsRead() {
        User user = securityUtils.getCurrentUser();
        userNotificationRepository.markAllAsReadByUserId(user.getId());
    }

    public Page<NotificationResponse> getShopNotifications(Pageable pageable) {
        Shop shop = securityUtils.getCurrentUserShop();
        Page<ShopNotification> page = shopNotificationRepository.findByShopIdOrderByCreatedAtDesc(shop.getId(), pageable);
        return page.map(NotificationResponse::fromShop);
    }

    public long getUnreadShopCount() {
        Shop shop = securityUtils.getCurrentUserShop();
        return shopNotificationRepository.countByShopIdAndIsReadFalse(shop.getId());
    }

    @Transactional
    public NotificationResponse markShopAsRead(String id) {
        ShopNotification notification = shopNotificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shop notification not found"));
        Shop shop = securityUtils.getCurrentUserShop();
        if (!notification.getShop().getId().equals(shop.getId())) {
            throw new ForbiddenException("Not allowed");
        }
        notification.setRead(true);
        shopNotificationRepository.save(notification);
        return NotificationResponse.fromShop(notification);
    }

    @Transactional
    public void markAllShopAsRead() {
        Shop shop = securityUtils.getCurrentUserShop();
        shopNotificationRepository.markAllAsReadByShopId(shop.getId());
    }
}
