package com.haiemdavang.AnrealShop.mapper;

import com.haiemdavang.AnrealShop.dto.NoticeScope;
import com.haiemdavang.AnrealShop.modal.entity.notification.UserNotification;
import com.haiemdavang.AnrealShop.tech.kafka.dto.notice.NoticeMessage;
import com.haiemdavang.AnrealShop.modal.entity.notification.ShopNotification;
import org.springframework.stereotype.Service;

@Service
public final class NoticeMapper {

    public NoticeMessage toNoticeMessage(ShopNotification shopNotification, String receiveBy) {
        return NoticeMessage.builder()
                .id(shopNotification.getId())
                .content(shopNotification.getContent())
                .receiveBy(receiveBy)
                .redirectUrl(shopNotification.getRedirectUrl())
                .thumbnailUrl(shopNotification.getThumbnailUrl())
                .noticeScope(NoticeScope.SHOP)
                .createdAt(shopNotification.getCreatedAt())
                .build();
    }

    public NoticeMessage toNoticeMessage(UserNotification userNotification, String receiveBy) {
        return NoticeMessage.builder()
                .id(userNotification.getId())
                .content(userNotification.getContent())
                .receiveBy(receiveBy)
                .redirectUrl(userNotification.getRedirectUrl())
                .thumbnailUrl(userNotification.getThumbnailUrl())
                .noticeScope(NoticeScope.USER)
                .createdAt(userNotification.getCreatedAt())
                .build();
    }
}