package com.haiemdavang.AnrealShop.mapper;

import com.haiemdavang.AnrealShop.dto.NoticeScope;
import com.haiemdavang.AnrealShop.dto.notice.NoticeMessage;
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
}