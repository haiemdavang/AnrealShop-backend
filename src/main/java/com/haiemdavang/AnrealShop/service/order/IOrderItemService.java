package com.haiemdavang.AnrealShop.service.order;

import com.haiemdavang.AnrealShop.dto.order.OrderRejectRequest;
import com.haiemdavang.AnrealShop.dto.order.search.ModeType;
import com.haiemdavang.AnrealShop.dto.order.search.OrderCountType;
import com.haiemdavang.AnrealShop.dto.order.search.PreparingStatus;
import com.haiemdavang.AnrealShop.dto.order.search.SearchType;
import com.haiemdavang.AnrealShop.modal.entity.order.Order;
import com.haiemdavang.AnrealShop.modal.entity.order.OrderItem;
import com.haiemdavang.AnrealShop.modal.entity.order.OrderItemTrack;
import com.haiemdavang.AnrealShop.modal.entity.shop.ShopOrder;
import com.haiemdavang.AnrealShop.modal.enums.CancelBy;
import com.haiemdavang.AnrealShop.modal.enums.OrderTrackStatus;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface IOrderItemService {

    void insertOrderItemTrack(Set<OrderItem> orderItems, Order newOrder);

    @Transactional
    void confirmOrderItems(Set<OrderItem> orderItems, OrderTrackStatus newStatus);

    List<OrderItem> getListOrderItems(ModeType mode, List<String> idShopOrders, String productName, SearchType searchType, String status, LocalDateTime localDateTime, LocalDateTime dateTime, OrderCountType orderType);

    void rejectOrderItemByIds(OrderRejectRequest orderRejectRequest, CancelBy cancelBy);

    ShopOrder rejectOrderItemById(String orderItemId, String reason, CancelBy cancelBy);

    List<OrderItem> getForShipment(@NotEmpty(message = "{SHIPMENT_SHOP_ORDER_IDS_NOT_EMPTY}") List<String> shopOrderIds);

    List<OrderItem> getByShopOrderIdIn(List<String> shopOrderIds);

    List<OrderItem> getListOrderItems(List<String> idShopOrders, String search, SearchType searchType, String status);
}
