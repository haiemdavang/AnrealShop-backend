package com.haiemdavang.AnrealShop.tech.kafka.dto.notice;

public final class NoticeTemplate {

    private NoticeTemplate() {}

    private static SimpleNoticeMessage toSimpleNoticeMessage(String content, NoticeTemplateType type) {
        return SimpleNoticeMessage.builder()
                .content(content)
                .noticeTemplateType(type)
                .build();
    }



    public static SimpleNoticeMessage newOrderForShop(String orderId) {
        String content = String.format(orderId);
        return toSimpleNoticeMessage(content, NoticeTemplateType.NEW_ORDER_FOR_SHOP);
    }

    public static SimpleNoticeMessage buildNoticeShopOrderPickedUp(String shopOrderId) {
        String content = String.format(shopOrderId);
        return toSimpleNoticeMessage(content, NoticeTemplateType.ORDER_SHIPPED);
    }

    public static SimpleNoticeMessage buildNoticeShopOrderOutForDelivery(String shopOrderId) {
        String content = String.format(shopOrderId);
        return toSimpleNoticeMessage(content, NoticeTemplateType.ORDER_DELIVERING);
    }
}