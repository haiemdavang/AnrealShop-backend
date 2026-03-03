package com.haiemdavang.AnrealShop.dto.order;
import com.haiemdavang.AnrealShop.modal.enums.OrderTrackStatus;

public interface OrderItemReviewProjection {
    String getOrderItemId();
    OrderTrackStatus getStatus();
    String getUserId();
    String getProductId();
    String getShopOrderId();
}
