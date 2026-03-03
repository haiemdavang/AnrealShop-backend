package com.haiemdavang.AnrealShop.service.order;

import com.haiemdavang.AnrealShop.dto.checkout.CheckoutRequestDto;
import com.haiemdavang.AnrealShop.dto.checkout.CheckoutResponseDto;
import com.haiemdavang.AnrealShop.dto.checkout.ItemProductCheckoutDto;
import com.haiemdavang.AnrealShop.dto.order.UserOrderItemDto;
import com.haiemdavang.AnrealShop.dto.order.UserOrderListResponse;
import com.haiemdavang.AnrealShop.dto.order.search.SearchType;
import com.haiemdavang.AnrealShop.dto.payment.PaymentRequestDto;
import com.haiemdavang.AnrealShop.dto.payment.PaymentResponseDto;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.mapper.OrderMapper;
import com.haiemdavang.AnrealShop.modal.entity.address.ShopAddress;
import com.haiemdavang.AnrealShop.modal.entity.address.UserAddress;
import com.haiemdavang.AnrealShop.modal.entity.order.Order;
import com.haiemdavang.AnrealShop.modal.entity.order.OrderItem;
import com.haiemdavang.AnrealShop.modal.entity.order.Payment;
import com.haiemdavang.AnrealShop.modal.entity.product.ProductSku;
import com.haiemdavang.AnrealShop.modal.entity.shop.ShopOrder;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.modal.enums.OrderTrackStatus;
import com.haiemdavang.AnrealShop.modal.enums.PaymentStatus;
import com.haiemdavang.AnrealShop.modal.enums.PaymentType;
import com.haiemdavang.AnrealShop.modal.enums.ShopOrderStatus;
import com.haiemdavang.AnrealShop.repository.order.OrderRepository;
import com.haiemdavang.AnrealShop.repository.order.ShopOrderSpecification;
import com.haiemdavang.AnrealShop.security.SecurityUtils;
import com.haiemdavang.AnrealShop.service.IProductService;
import com.haiemdavang.AnrealShop.service.IReviewService;
import com.haiemdavang.AnrealShop.service.IShipmentService;
import com.haiemdavang.AnrealShop.service.payment.IPaymentService;
import com.haiemdavang.AnrealShop.service.payment.VNPayService;
import com.haiemdavang.AnrealShop.utils.ApplicationInitHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserOrderServiceImp implements IUserOrderService {
    private final OrderRepository orderRepository;
    private final IShopOrderService shopOrderService;
    private final IOrderItemService orderItemService;
    private final IPaymentService paymentService;
    private final IShipmentService shipmentService;
    private final IProductService productService;
    private final IReviewService reviewService;
    private final VNPayService vnPayService;
    private final SecurityUtils securityUtils;

    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public CheckoutResponseDto createOrderBankTran(CheckoutRequestDto requestDto, UserAddress userAddress, String ipAddress) {
        Order newOrder = this.createNewOrder(requestDto, userAddress, PaymentType.BANK_TRANSFER);

        PaymentRequestDto paymentRequestDto = PaymentRequestDto.builder()
                .orderId(newOrder.getId())
                .amount(newOrder.getGrandTotalAmount())
                .orderInfo("Nhap thong tin vao nhe hai")
                .build();

        return CheckoutResponseDto.createResponseForBankTransfer(newOrder.getId(), vnPayService.createPaymentUrl(paymentRequestDto, ipAddress));
    }

    @Override
    public CheckoutResponseDto createOrderCOD(CheckoutRequestDto requestDto, UserAddress userAddress) {
        Order newOrder = this.createNewOrder(requestDto, userAddress, PaymentType.COD);
        orderItemService.confirmOrderItems(newOrder.getOrderItems(), OrderTrackStatus.PENDING_CONFIRMATION);
        shopOrderService.confirmShopOrders(newOrder.getShopOrders(), ShopOrderStatus.PENDING_CONFIRMATION);
        return CheckoutResponseDto.createResponseForCashOnDelivery(newOrder.getId());
    }

    @Override
    @Transactional
    public void handleSuccessfulPayment(String orderId) {
        Order order = orderRepository.findWithPaymentById(orderId)
                .orElseThrow(() -> new BadRequestException("ORDER_NOT_FOUND"));

        paymentService.updatePayment(order.getPayment(), PaymentStatus.COMPLETED);

        orderItemService.confirmOrderItems(order.getOrderItems(), OrderTrackStatus.PENDING_CONFIRMATION);
        shopOrderService.confirmShopOrders(order.getShopOrders(), ShopOrderStatus.PENDING_CONFIRMATION);
    }

    @Override
    public void handleFailedPayment(String orderId, String responseCode) {

        Order order = orderRepository.findWithPaymentById(orderId)
                .orElseThrow(() -> new BadRequestException("ORDER_NOT_FOUND"));

        paymentService.updatePayment(order.getPayment(), PaymentStatus.FAILED);
    }

    @Override
    public PaymentResponseDto getPaymentResult(String orderId) {
        Order order = orderRepository.findWithPaymentById(orderId)
                .orElseThrow(() -> new BadRequestException("ORDER_NOT_FOUND"));

        Payment payment = order.getPayment();

        return PaymentResponseDto.builder()
                .orderId(order.getId())
                .amount(order.getGrandTotalAmount())
                .paymentGateway(payment.getGateway())
                .paymentStatus(payment.getStatus())
                .paymentMethod(payment.getType())
                .isTransfer(payment.getType().equals(PaymentType.BANK_TRANSFER))
                .orderDate(order.getCreatedAt())
                .orderDateExpiration(payment.getExpireAt())
                .build();
    }

    @Override
    public UserOrderListResponse getListOrderItems(int page, int limit, String status, String search, SearchType searchType, String sortBy) {
        String userId = securityUtils.getCurrentUser().getId();

        Specification<ShopOrder> orderSpecification = ShopOrderSpecification.filter(userId, status, search, searchType);
        Pageable pageable = PageRequest.of(page, limit, ApplicationInitHelper.getSortBy(sortBy));

        Page<ShopOrder> shopOrders = shopOrderService.gitListOrderForUser(orderSpecification, pageable);
        List<String> idShopOrders = shopOrders.stream().map(ShopOrder::getId).toList();
        Map<String, ShopOrder> mapShopOrders = shopOrders.stream().collect(Collectors.toMap(ShopOrder::getId, so -> so));

        Set<UserOrderItemDto> orderItemDtoSet = new HashSet<>();

        List<OrderItem> orderItems = orderItemService.getListOrderItems(idShopOrders, search, searchType, status);
        Map<String, Set<OrderItem>> mapOrderItems = orderItems.stream().collect(
                Collectors.groupingBy(oi -> oi.getShopOrder().getId(), Collectors.toSet())
        ).entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
        );

        // Batch lấy danh sách order item đã được review
        Set<String> allOrderItemIds = orderItems.stream()
                .map(OrderItem::getId)
                .collect(Collectors.toSet());
        Set<String> reviewedOrderItemIds = reviewService.getReviewedOrderItemIds(allOrderItemIds);

        for (String idShopOrder : mapOrderItems.keySet()){
            ShopOrder shopOrder = mapShopOrders.get(idShopOrder);
            Set<OrderItem> orderItemsOfShopOrder = mapOrderItems.get(idShopOrder);
            if (shopOrder == null || orderItemsOfShopOrder == null) continue;
            UserOrderItemDto orderItemDto = orderMapper.toUserOrderItemDto(shopOrder, orderItemsOfShopOrder);

            // Đánh dấu isReviewed cho từng product item
            orderItemDto.getProductOrderItemDtoSet().forEach(item ->
                    item.setReviewed(reviewedOrderItemIds.contains(item.getOrderItemId()))
            );

            orderItemDtoSet.add(orderItemDto);
        }

        return  UserOrderListResponse.builder()
                .currentPage(shopOrders.getNumber())
                .totalPages(shopOrders.getTotalPages())
                .totalCount(shopOrders.getTotalElements())
                .orderItemDtoSet(orderItemDtoSet)
                .build();
    }


    private Order createNewOrder(CheckoutRequestDto requestDto, UserAddress userAddress, PaymentType paymentType) {
        User user = securityUtils.getCurrentUser();

        Map<String, Integer> itemRequests = requestDto.getItems().stream().collect(
                Collectors.toMap(ItemProductCheckoutDto::getProductSkuId, ItemProductCheckoutDto::getQuantity));
        List<ProductSku> productSkus = productService.findByProductSkuIdIn(itemRequests.keySet());
        Map<ProductSku, Integer> mapProductSkuWithQuantityOrder = productSkus.stream().collect(
                Collectors.toMap(t -> t, t -> itemRequests.get(t.getId())));

        Order order = Order.builder()
                .user(user)
                .shippingAddress(userAddress)
                .build();

        long subTotalAmount = 0L;
        for (ProductSku productSku : productSkus) {
            Integer quantity = itemRequests.get(productSku.getId());

            subTotalAmount += productSku.getPrice() * quantity;
            OrderItem orderItem = OrderItem.builder()
                    .productSku(productSku)
                    .quantity(quantity)
                    .price(productSku.getPrice())
                    .build();
            order.addOrderItem(orderItem);
        }

        Map<ShopAddress, Long> mapFeeForShops = shipmentService.getShippingFee(userAddress, mapProductSkuWithQuantityOrder);
        long totalShippingFee = 0L;

        for (ShopAddress shopAddress : mapFeeForShops.keySet()) {
            ShopOrder shopOrder = ShopOrder.builder()
                    .shop(shopAddress.getShop())
                    .user(user).build();

            Long shippingFee = mapFeeForShops.get(shopAddress);

            long totalForShop = 0L;
            long totalWeightForShop = 0L;

            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getProductSku().getProduct().getShop().equals(shopAddress.getShop())){
                    totalForShop += orderItem.getQuantity() * orderItem.getPrice();
                    totalWeightForShop += orderItem.getQuantity() * orderItem.getProductSku().getProduct().getWeight();
                    shopOrder.addOrderItems(orderItem);
                }
            }
            totalShippingFee += shippingFee;

            shopOrder.setShippingFee(shippingFee);
            shopOrder.setTotalAmount(totalForShop);
            shopOrder.setShippingAddress(shopAddress);
            shopOrder.setTotalWeight(totalWeightForShop);

            order.addShopOrder(shopOrder);
        }


        long grandTotalAmount = subTotalAmount + totalShippingFee;
        Payment payment = paymentService.createPayment(grandTotalAmount, requestDto.getPaymentGateway(), paymentType);

        order.setPayment(payment);
        order.setSubTotalAmount(subTotalAmount);
        order.setTotalShippingFee(totalShippingFee);
        order.setGrandTotalAmount(grandTotalAmount);

        Order newOrder = orderRepository.save(order);
        shopOrderService.insertShopOrderTrack(newOrder.getShopOrders(), newOrder);
        orderItemService.insertOrderItemTrack(newOrder.getOrderItems(), newOrder);
//        productService.decreaseProductSkuQuantity(orderItems);
        return newOrder;
    }
}
