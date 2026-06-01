package com.haiemdavang.AnrealShop.service.order;

import com.haiemdavang.AnrealShop.dto.order.ProductOrderItemDto;
import com.haiemdavang.AnrealShop.mapper.OrderMapper;
import com.haiemdavang.AnrealShop.modal.entity.order.OrderItem;
import com.haiemdavang.AnrealShop.modal.entity.shop.ShopOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImp {
    private final IShopOrderService shopOrderServiceImp;
    private final IOrderItemService orderItemService;

    private final OrderMapper orderMapper;

    public List<ShopOrder> getShopOrderByOrderId(String orderId) {
        return shopOrderServiceImp.getShopOrdersByOrderId(orderId);
    }

    public Set<ProductOrderItemDto> getProductOrderItemByShopOrder(String id) {
        List<OrderItem> orderItems = orderItemService.getByShopOrderIdIn(List.of(id));
        return orderItems.stream().map(orderMapper::toOrderItemDto).collect(java.util.stream.Collectors.toSet());
    }

}
