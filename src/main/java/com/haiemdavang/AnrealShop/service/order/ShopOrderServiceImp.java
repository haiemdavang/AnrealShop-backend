package com.haiemdavang.AnrealShop.service.order;

import com.haiemdavang.AnrealShop.dto.order.*;
import com.haiemdavang.AnrealShop.dto.order.search.ModeType;
import com.haiemdavang.AnrealShop.dto.order.search.OrderCountType;
import com.haiemdavang.AnrealShop.dto.order.search.PreparingStatus;
import com.haiemdavang.AnrealShop.dto.order.search.SearchType;
import com.haiemdavang.AnrealShop.dto.shipping.BaseCreateShipmentRequest;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.mapper.OrderMapper;
import com.haiemdavang.AnrealShop.mapper.ShipmentMapper;
import com.haiemdavang.AnrealShop.modal.entity.order.Order;
import com.haiemdavang.AnrealShop.modal.entity.order.OrderItem;
import com.haiemdavang.AnrealShop.modal.entity.shipping.Shipping;
import com.haiemdavang.AnrealShop.modal.entity.shop.ShopOrder;
import com.haiemdavang.AnrealShop.modal.entity.shop.ShopOrderTrack;
import com.haiemdavang.AnrealShop.modal.enums.CancelBy;
import com.haiemdavang.AnrealShop.modal.enums.OrderTrackStatus;
import com.haiemdavang.AnrealShop.modal.enums.ShopOrderStatus;
import com.haiemdavang.AnrealShop.repository.order.ShopOrderRepository;
import com.haiemdavang.AnrealShop.repository.order.ShopOrderSpecification;
import com.haiemdavang.AnrealShop.security.SecurityUtils;
import com.haiemdavang.AnrealShop.service.IShipmentService;
import com.haiemdavang.AnrealShop.utils.ApplicationInitHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopOrderServiceImp implements IShopOrderService {
    private final ShopOrderRepository shopOrderRepository;
    private final SecurityUtils securityUtils;
    private final OrderMapper orderMapper;
    private final IOrderItemService orderItemService;
    private final IShipmentService shipmentService;
    private final ShipmentMapper shipmentMapper;

    private final double SHIPPING_FEE_RATE = 0.2;


    @Override
    @Transactional
    public void insertShopOrderTrack(Set<ShopOrder> shopOrders, Order newOrder) {
        for (ShopOrder shopOrder : shopOrders) {
            ShopOrderTrack shopOrderTrack = new ShopOrderTrack(shopOrder);
            shopOrder.addTrackingHistory(shopOrderTrack);
        }
        shopOrderRepository.saveAll(shopOrders);
    }

    @Override
    public Set<OrderStatusDto> getShopFilterMetaData(String shopId, String search, SearchType searchType) {
        LocalDateTime now = LocalDate.now().atTime(23, 59, 59);
        shopId = securityUtils.getCurrentUserShop().getId();
        Specification<ShopOrder> spec = ShopOrderSpecification.filter(shopId, ModeType.HOME, now.minusMonths(2), now, search, searchType);
        List<ShopOrder> orders = shopOrderRepository.findAll(spec);

        Map<ShopOrderStatus, Integer> statusMap = orders.stream().collect(Collectors.toMap(
                ShopOrder::getStatus,
                so -> 1,
                Integer::sum
        ));

        Set<OrderStatusDto> result = new HashSet<>();
        int total = 0;
        for (ShopOrderStatus data : ShopOrderStatus.values()){
            if (data.equals(ShopOrderStatus.INIT_PROCESSING)) continue;
            Integer count = statusMap.get(data);
            if (count == null){
                result.add(OrderStatusDto.createNewOrderStatusDto(data));
            }else {
                total += count;
                result.add(OrderStatusDto.convertToOrderStatsDto(data, count));
            }
        }
        result.add(OrderStatusDto.builder().id("ALL").name("Tất cả").count(total).build());

        return result;
    }

    @Override
    public MyShopOrderListResponse getListOrderItems(int page, int limit, ModeType mode, String status, String search, SearchType searchType, LocalDateTime confirmSD, LocalDateTime confirmED, OrderCountType orderType, PreparingStatus preparingStatus, String sortBy) {
        LocalDateTime now =  LocalDate.now().atTime(23, 59, 59);
        String shopId = securityUtils.getCurrentUserShop().getId();
        Specification<ShopOrder> orderSpecification = ShopOrderSpecification.filter(shopId, mode, now.minusMonths(2), now, status, search, searchType, confirmSD, confirmED, orderType, preparingStatus);
        Pageable pageable = PageRequest.of(page, limit, ApplicationInitHelper.getSortBy(sortBy));

        Page<ShopOrder> shopOrders = shopOrderRepository.findAll(orderSpecification, pageable);
        List<String> idShopOrders = shopOrders.stream().map(ShopOrder::getId).toList();
        List<OrderItemDto> orderItemDtoSet = new ArrayList<>();
        if (!idShopOrders.isEmpty()) {
            Map<String, ShopOrder> mapShopOrders = shopOrders.stream().collect(Collectors.toMap(ShopOrder::getId, so -> so));
            List<OrderItem> orderItems = orderItemService.getListOrderItems(mode, idShopOrders, search, searchType, status, confirmSD, confirmED, orderType);
            Map<String, Set<OrderItem>> mapOrderItems = orderItems.stream().collect(
                    Collectors.groupingBy(oi -> oi.getShopOrder().getId(), Collectors.toSet())
            ).entrySet().stream().collect(
                    Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
            );

            for (String idShopOrder : idShopOrders){
                if (!mapOrderItems.containsKey(idShopOrder)) continue;
                ShopOrder shopOrder = mapShopOrders.get(idShopOrder);
                Set<OrderItem> orderItemsOfShopOrder = mapOrderItems.get(idShopOrder);
                if (shopOrder == null || orderItemsOfShopOrder == null) continue;
                OrderItemDto orderItemDto = orderMapper.toOrderItemDto(shopOrder, orderItemsOfShopOrder);
                orderItemDtoSet.add(orderItemDto);
            }
        }

        return  MyShopOrderListResponse.builder()
                .currentPage(shopOrders.getNumber())
                .totalPages(shopOrders.getTotalPages())
                .totalCount(shopOrders.getTotalElements())
                .orderItemDtoSet(orderItemDtoSet)
                .build();
    }

    @Override
    public Page<ShopOrder> gitListOrderForUser(Specification<ShopOrder> orderSpecification, Pageable pageable) {
        return shopOrderRepository.findAll(orderSpecification, pageable);
    }

    @Transactional
    @Override
    public void rejectOrderById(String shopOrderId, String reason, CancelBy cancelBy) {
        if (shopOrderId == null || shopOrderId.isEmpty() || !shopOrderRepository.existsById(shopOrderId)) {
            throw new BadRequestException("ORDER_NOT_FOUND");
        }

        ShopOrder shopOrder = shopOrderRepository.findWithOrderItemById(shopOrderId);

        shopOrder.setStatus(ShopOrderStatus.CLOSED);

        shopOrder.getOrderItems().stream()
                .filter(ot -> ot.getStatus().equals(OrderTrackStatus.PENDING_CONFIRMATION) || ot.getStatus().equals(OrderTrackStatus.WAIT_SHIPMENT))
                .forEach(ot -> orderItemService.rejectOrderItemById(ot.getId(), reason, cancelBy));

        shopOrderRepository.save(shopOrder);
    }

    @Override
    public UserOrderDetailDto getShopOrderForUser(String shopOrderId) {
        if (shopOrderId == null || shopOrderId.isEmpty() || !shopOrderRepository.existsById(shopOrderId)) {
            throw new BadRequestException("ORDER_NOT_FOUND");
        }

        ShopOrder shopOrder = shopOrderRepository.findWithFullInfoById(shopOrderId);

        UserOrderDetailDto orderDetailDto = orderMapper.toUserOrderDetailDto(shopOrder, shopOrder.getOrderItems(), shopOrder.getOrder().getShippingAddress());
        Shipping shipping = shipmentService.getShippingByShopOrderId(shopOrderId);

        long totalProductCost = shopOrder.getTotalAmount() + shopOrder.getShippingFee();

        Long getDiscountShippingFee = getDiscountShippingFee(shopOrder.getShippingFee());
        Long shippingFee = orderDetailDto.getShippingFee();

        orderDetailDto.setTotalProductCost(totalProductCost);
        orderDetailDto.setShippingDiscount(getDiscountShippingFee);
        orderDetailDto.setTotalShippingCost(Math.max(0L, shippingFee - getDiscountShippingFee));
        orderDetailDto.setTotalCost(totalProductCost + Math.max(0L, shippingFee - getDiscountShippingFee));
        if (shipping != null)
            orderDetailDto.setOrderHistory(shipmentMapper.toHistoryTrackDto(shipping.getTrackingHistory()));

        return orderDetailDto;
    }

    @Override
    public OrderDetailDto getShopOrder(String shopOrderId) {
        if (shopOrderId == null || shopOrderId.isEmpty() || !shopOrderRepository.existsById(shopOrderId)) {
            throw new BadRequestException("ORDER_NOT_FOUND");
        }

        ShopOrder shopOrder = shopOrderRepository.findWithFullInfoById(shopOrderId);

        OrderDetailDto orderDetailDto = orderMapper.orderDetailDto(shopOrder);

        long totalProductCost = shopOrder.getTotalAmount() - shopOrder.getShippingFee();
        Long shippingDiscount = getDiscountShippingFee(shopOrder.getShippingFee());
        Long shippingFee = orderDetailDto.getShippingFee();

        orderDetailDto.setTotalProductCost(totalProductCost);
        orderDetailDto.setShippingDiscount(shippingDiscount);
        orderDetailDto.setTotalShippingCost(Math.max(0L, shippingFee - shippingDiscount));
        Long FIXED_FEE = 30L;
        orderDetailDto.setFixedFeeRate(FIXED_FEE);
        Long SERVICE_FEE = 42L;
        orderDetailDto.setServiceFeeRate(SERVICE_FEE);
        Long PAYMENT_FEE = 74L;
        orderDetailDto.setPaymentFeeRate(PAYMENT_FEE);
        orderDetailDto.setRevenue(totalProductCost - Math.round(totalProductCost * (SHIPPING_FEE_RATE + FIXED_FEE + SERVICE_FEE)/100));

        return orderDetailDto;
    }

    @Transactional
    @Override
    public void confirmShopOrders(Set<ShopOrder> shopOrders, ShopOrderStatus newStatus) {
        Set<ShopOrder> results = new HashSet<>();
        for (ShopOrder item : shopOrders) {
            ShopOrderTrack latestTrack = item.getTrackingHistory().stream()
                    .max(Comparator.comparing(ShopOrderTrack::getUpdatedAt))
                    .orElseThrow(() -> new BadRequestException("NO_TRACKING_HISTORY"));

            if (latestTrack.getStatus().equals(newStatus))
                break;

            item.setStatus(newStatus);

            results.add(item);
        }

        shopOrderRepository.saveAll(results);
    }

    @Override
    @Transactional
    public void approvalShopOrderById(String shopOrderId) {
        if (shopOrderId == null || shopOrderId.isEmpty() || !shopOrderRepository.existsById(shopOrderId)) {
            throw new BadRequestException("ORDER_NOT_FOUND");
        }

        ShopOrder shopOrder = shopOrderRepository.findWithFullOrderItemById(shopOrderId);
        shopOrderRepository.save(updateStatusShopOrder(shopOrder, ShopOrderStatus.CONFIRMED));
    }

    @Override
    @Transactional
    public void rejectOrderItemById(String orderItemId, String reason, CancelBy cancelBy) {
        ShopOrder shopOrder = orderItemService.rejectOrderItemById(orderItemId, reason, cancelBy);
        handleMapStatus(shopOrder);
    }

    @Override
    @Transactional
    public List<String> confirmOrders(ShopOrderStatus statusFilter, ShopOrderStatus newStatus) {
        List<ShopOrder> shopOrder = shopOrderRepository.findAllByStatus(statusFilter);
        if (shopOrder.isEmpty()) {
            return null;
        }
        List<String> shopOrderIs = shopOrder.stream().map(ShopOrder::getId).toList();

        updateStatus(shopOrderIs, newStatus);
        return shopOrderIs;
    }


    @Override
    @Transactional
    public void availableForShipById(String shopOrderId, BaseCreateShipmentRequest request) {
        if (shopOrderId == null || shopOrderId.isEmpty() || !shopOrderRepository.existsById(shopOrderId)) {
            throw new BadRequestException("ORDER_NOT_FOUND");
        }

        ShopOrder shopOrder = shopOrderRepository.findWithOrderItemById(shopOrderId);
        shipmentService.createShipments(shopOrderId, request);
        shopOrderRepository.save(updateStatusShopOrder(shopOrder, ShopOrderStatus.PREPARING));
    }

    @Override
    @Transactional
    public void updateStatus(List<String> shopOrderIds, ShopOrderStatus status) {
        Set<ShopOrder> shopOrders = shopOrderRepository.findByIdIn(shopOrderIds);
        if (shopOrders.size() != shopOrderIds.size()) {
            throw new BadRequestException("SOME_ORDER_NOT_FOUND");
        }
        shopOrders.forEach(so -> updateStatusShopOrder(so, status));
        shopOrderRepository.saveAll(shopOrders);
    }

    @Override
    public List<ShopOrder> getShopOrdersByOrderId(String content) {
        return shopOrderRepository.findByOrderId(content);
    }


    private  void handleMapStatus(ShopOrder shopOrder) {
        if (shopOrder.getOrderItems().stream().allMatch(ot -> ot.getStatus().equals(OrderTrackStatus.CANCELED))) {
            shopOrder.setStatus(ShopOrderStatus.CLOSED);
            shopOrderRepository.save(shopOrder);
        }
    }

    private Long getDiscountShippingFee(Long shippingFee) {
        return shippingFee == 0L ? 0L : (long) (shippingFee * SHIPPING_FEE_RATE);
    }

    private ShopOrder updateStatusShopOrder(ShopOrder shopOrder, ShopOrderStatus newStatus) {
        shopOrder.setStatus(newStatus);

        switch (newStatus) {
            case CONFIRMED ->
                    shopOrder.getOrderItems().stream()
                            .filter(ot -> ot.getStatus().equals(OrderTrackStatus.PENDING_CONFIRMATION))
                            .forEach(ot -> ot.setStatus(OrderTrackStatus.PREPARING));
            case PREPARING ->
                    shopOrder.getOrderItems().stream()
                            .filter(ot -> ot.getStatus().equals(OrderTrackStatus.PREPARING))
                            .forEach(ot -> ot.setStatus(OrderTrackStatus.WAIT_SHIPMENT));
            case SHIPPING ->
                    shopOrder.getOrderItems().stream()
                            .filter(ot -> ot.getStatus().equals(OrderTrackStatus.WAIT_SHIPMENT))
                            .forEach(ot -> ot.setStatus(OrderTrackStatus.SHIPPING));
            case DELIVERED ->
                    shopOrder.getOrderItems().stream()
                            .filter(ot -> ot.getStatus().equals(OrderTrackStatus.SHIPPING))
                            .forEach(ot -> ot.setStatus(OrderTrackStatus.DELIVERED));
        }
        return shopOrder;
    }
}
