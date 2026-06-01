package com.haiemdavang.AnrealShop.service.order;

import com.haiemdavang.AnrealShop.dto.order.OrderRejectRequest;
import com.haiemdavang.AnrealShop.dto.order.search.ModeType;
import com.haiemdavang.AnrealShop.dto.order.search.OrderCountType;
import com.haiemdavang.AnrealShop.dto.order.search.SearchType;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.modal.entity.order.Order;
import com.haiemdavang.AnrealShop.modal.entity.order.OrderItem;
import com.haiemdavang.AnrealShop.modal.entity.order.OrderItemTrack;
import com.haiemdavang.AnrealShop.modal.entity.shop.ShopOrder;
import com.haiemdavang.AnrealShop.modal.enums.CancelBy;
import com.haiemdavang.AnrealShop.modal.enums.OrderTrackStatus;
import com.haiemdavang.AnrealShop.repository.order.OrderItemRepository;
import com.haiemdavang.AnrealShop.repository.order.OrderItemSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImp implements IOrderItemService {
    private final OrderItemRepository orderItemRepository;


    @Override
    @Transactional
    public void insertOrderItemTrack(Set<OrderItem> orderItems, Order newOrder) {
        for (OrderItem orderItem : orderItems) {
            OrderItemTrack orderItemTrack = new OrderItemTrack(orderItem);
            orderItem.addTrackingHistory(orderItemTrack);
        }
        orderItemRepository.saveAll(orderItems);
    }

    @Transactional
    @Override
    public void confirmOrderItems(Set<OrderItem> orderItems, OrderTrackStatus newStatus) {
        Set<OrderItem> orderItemsSet = new HashSet<>();
        for (OrderItem item : orderItems) {
            OrderItemTrack latestTrack = item.getTrackingHistory().stream()
                .max(Comparator.comparing(OrderItemTrack::getUpdatedAt))
                .orElseThrow(() -> new BadRequestException("NO_TRACKING_HISTORY"));

            if (latestTrack.getStatus().equals(newStatus))
                break;

            item.setStatus(newStatus);

            orderItemsSet.add(item);
        }

        orderItemRepository.saveAll(orderItemsSet);
    }

    @Override
    public List<OrderItem> getListOrderItems(ModeType mode, List<String> idShopOrders, String search, SearchType searchType, String status, LocalDateTime localDateTime, LocalDateTime dateTime, OrderCountType orderType) {
        Specification<OrderItem> spec = OrderItemSpecification.filter(mode, idShopOrders, search, searchType, status, orderType);
        return orderItemRepository.findAll(spec);
    }

    @Override
    @Transactional
    public void rejectOrderItemByIds(OrderRejectRequest orderRejectRequests, CancelBy cancelBy) {
        if(orderRejectRequests.getReason() == null || orderRejectRequests.getReason().isEmpty())
            throw new BadRequestException("REASON_EMPTY");

        List<OrderItem> orderItems = orderItemRepository.findByIdIn(orderRejectRequests.getIds());

        if (orderRejectRequests.getIds().size() != orderItems.size())
            throw new BadRequestException("ID_NOT_MATCH");

        for (OrderItem orderItem : orderItems) {
            orderItem.setStatus(OrderTrackStatus.CANCELED);
            orderItem.setCancelReason(orderRejectRequests.getReason());
            orderItem.setCanceledBy(cancelBy);
        }

        orderItemRepository.saveAll(orderItems);

    }

    @Override
    @Transactional
    public ShopOrder rejectOrderItemById(String orderItemId, String reason, CancelBy cancelBy) {
        if(reason == null || reason.isEmpty())
            throw new BadRequestException("REASON_EMPTY");

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new BadRequestException("ORDER_ITEM_NOT_FOUND"));

        orderItem.setStatus(OrderTrackStatus.CANCELED);
        orderItem.setCancelReason(reason);
        orderItem.setCanceledBy(cancelBy);
        return orderItemRepository.save(orderItem).getShopOrder();
    }

    @Override
    public List<OrderItem> getForShipment(List<String> shopOrderIds) {
        List<OrderItem> orderItems = orderItemRepository.findByShopOrderIdInAndStatus(shopOrderIds, OrderTrackStatus.PREPARING);
        if (orderItems.isEmpty())
            throw new BadRequestException("SHOP_ORDER_ID_NOT_MATCH");
        return orderItems;
    }

    @Override
    public List<OrderItem> getByShopOrderIdIn(List<String> shopOrderIds) {
        List<OrderItem> orderItems = orderItemRepository.findByShopOrderIdIn(shopOrderIds);
        if (orderItems.isEmpty())
            throw new BadRequestException("SHOP_ORDER_ID_NOT_MATCH");
        return orderItems;
    }

    @Override
    public List<OrderItem> getListOrderItems(List<String> idShopOrders, String search, SearchType searchType, String status) {
        Specification<OrderItem> spec = OrderItemSpecification.filter(idShopOrders, search, searchType, status);
        return orderItemRepository.findAll(spec);
    }


}
