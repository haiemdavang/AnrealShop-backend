package com.haiemdavang.AnrealShop.dto.notice;

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

    public static SimpleNoticeMessage updateStatusOrder(String orderItemId) {
        String content = String.format(orderItemId);
        return toSimpleNoticeMessage(content, NoticeTemplateType.ORDER_STATUS_UPDATE);
    }


}