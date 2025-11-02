package com.haiemdavang.AnrealShop.dto.notice;

import com.haiemdavang.AnrealShop.modal.entity.notification.ShopNotification;
import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;

public final class NoticeMyShopTemplate {

    public static ShopNotification newOrderForShop(Shop shop, String orderId) {
        String content = "Bạn có đơn hàng mới truy cập để xem chi tiết!" ;
        String redirectUrl = "/myshop/orders/" + orderId;
        return ShopNotification.builder()
                .content(content)
                .redirectUrl(redirectUrl)
                .shop(shop)
                .isRead(false)
                .build();
    }
}