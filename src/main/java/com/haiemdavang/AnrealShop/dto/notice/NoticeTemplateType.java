package com.haiemdavang.AnrealShop.dto.notice;

import lombok.Getter;

@Getter
public enum NoticeTemplateType {
//    ORDER_PLACED,
//    ORDER_STATUS_CHANGED,
//    PROMOTION,
//    ADMIN_ALERT,
//    CHAT,
//    SYSTEM,

    NEW_ORDER_FOR_SHOP,
    ORDER_STATUS_UPDATE,
    ORDER_CANCELLED_BY_CUSTOMER,
    NEW_REVIEW_FOR_SHOP,
    NEW_CHAT_MESSAGE_FROM_CUSTOMER,
    PRODUCT_VIOLATION_WARNING,

    // Thông báo cho User (Khách hàng)
    ORDER_CONFIRMED_BY_SHOP,
    ORDER_SHIPPED,
    ORDER_DELIVERED,
    ORDER_CANCELLED_BY_SHOP,
    NEW_CHAT_MESSAGE_FROM_SHOP,
    PROMOTION_FOR_USER
}