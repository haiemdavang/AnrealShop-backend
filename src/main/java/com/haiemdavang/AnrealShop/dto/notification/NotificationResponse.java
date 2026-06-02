package com.haiemdavang.AnrealShop.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private String id;
    private String content;
    private String thumbnailUrl;
    private String redirectUrl;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponse fromUser(com.haiemdavang.AnrealShop.modal.entity.notification.UserNotification notification) {
        if (notification == null) return null;
        return NotificationResponse.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .thumbnailUrl(notification.getThumbnailUrl())
                .redirectUrl(notification.getRedirectUrl())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    public static NotificationResponse fromShop(com.haiemdavang.AnrealShop.modal.entity.notification.ShopNotification notification) {
        if (notification == null) return null;
        return NotificationResponse.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .thumbnailUrl(notification.getThumbnailUrl())
                .redirectUrl(notification.getRedirectUrl())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
