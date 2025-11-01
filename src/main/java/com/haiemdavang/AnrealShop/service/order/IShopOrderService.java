package com.haiemdavang.AnrealShop.service.order;

import com.haiemdavang.AnrealShop.dto.order.MyShopOrderListResponse;
import com.haiemdavang.AnrealShop.dto.order.OrderDetailDto;
import com.haiemdavang.AnrealShop.dto.order.OrderStatusDto;
import com.haiemdavang.AnrealShop.dto.order.UserOrderDetailDto;
import com.haiemdavang.AnrealShop.dto.order.search.ModeType;
import com.haiemdavang.AnrealShop.dto.order.search.OrderCountType;
import com.haiemdavang.AnrealShop.dto.order.search.PreparingStatus;
import com.haiemdavang.AnrealShop.dto.order.search.SearchType;
import com.haiemdavang.AnrealShop.dto.shipping.BaseCreateShipmentRequest;
import com.haiemdavang.AnrealShop.modal.entity.order.Order;
import com.haiemdavang.AnrealShop.modal.entity.shop.ShopOrder;
import com.haiemdavang.AnrealShop.modal.enums.CancelBy;
import com.haiemdavang.AnrealShop.modal.enums.ShopOrderStatus;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface IShopOrderService {
    OrderDetailDto getShopOrder(String shopOrderId);

    Set<OrderStatusDto> getShopFilterMetaData(String shopId, String search, SearchType searchType);

    void insertShopOrderTrack(Set<ShopOrder> shopOrders, Order newOrder);

    @Transactional
    void confirmShopOrders(Set<ShopOrder> shopOrders, ShopOrderStatus newStatus);

    void approvalShopOrderById(String shopOrderId);

    void rejectOrderItemById(String orderItemId, String reason, CancelBy cancelBy);

    MyShopOrderListResponse getListOrderItems(int page, int limit, ModeType mode, String status, String search, SearchType searchType, LocalDateTime confirmSD, LocalDateTime confirmED, OrderCountType orderType, PreparingStatus preparingStatus, String sortBy);

    Page<ShopOrder> gitListOrderForUser(Specification<ShopOrder> orderSpecification, Pageable pageable);

    void rejectOrderById(String shopOrderId, String reason, CancelBy cancelBy);

    UserOrderDetailDto getShopOrderForUser(String shopOrderId);

    List<String> confirmOrders(ShopOrderStatus statusFilter, ShopOrderStatus newStatus);

    void availableForShipById(String shopOrderId, BaseCreateShipmentRequest request);

    void updateStatus(@NotEmpty(message = "{SHIPMENT_SHOP_ORDER_IDS_NOT_EMPTY}") List<String> shopOrderIds, ShopOrderStatus preparing);


    List<ShopOrder> getShopOrdersByOrderId(String content);
}