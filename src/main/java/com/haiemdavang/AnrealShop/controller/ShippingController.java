package com.haiemdavang.AnrealShop.controller;

import com.haiemdavang.AnrealShop.dto.shipping.BaseCreateShipmentRequest;
import com.haiemdavang.AnrealShop.dto.shipping.CartShippingFee;
import com.haiemdavang.AnrealShop.dto.shipping.CreateShipmentRequest;
import com.haiemdavang.AnrealShop.dto.shipping.MyShopShippingListResponse;
import com.haiemdavang.AnrealShop.dto.shipping.search.CheckoutShippingFee;
import com.haiemdavang.AnrealShop.dto.shipping.search.PreparingStatus;
import com.haiemdavang.AnrealShop.dto.shipping.search.SearchTypeShipping;
import com.haiemdavang.AnrealShop.modal.enums.CancelBy;
import com.haiemdavang.AnrealShop.modal.enums.ShopOrderStatus;
import com.haiemdavang.AnrealShop.service.serviceInter.IShipmentService;
import com.haiemdavang.AnrealShop.service.order.IShopOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shipping")
public class ShippingController {
    private final IShipmentService shipmentService;
    private final IShopOrderService orderService;

    @GetMapping("/my-shop")
    public ResponseEntity<MyShopShippingListResponse> getListForShop(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "order_code") SearchTypeShipping searchTypeShipping,
            @RequestParam(required = false, defaultValue = "all") PreparingStatus preparingStatus,
            @RequestParam(required = false, defaultValue = "newest") String sortBy
    ) {
        MyShopShippingListResponse response = shipmentService.getListForShop(page, limit, search, searchTypeShipping, preparingStatus, sortBy);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/my-shop/reject-shipping/{shippingId}")
    public ResponseEntity<?> getListForShop(@PathVariable String shippingId, @RequestBody String reason) {
        String shopOrderId = shipmentService.rejectById(shippingId, reason);
        orderService.rejectOrderById(shopOrderId, reason, CancelBy.SHOP);
        return ResponseEntity.ok(Map.of("message", "reject order successfully!"));
    }

    @PostMapping("/fee-for-cart")
    public ResponseEntity<List<CartShippingFee>> getFeeForCart(@RequestBody List<String> cartItemIds) {
        List<CartShippingFee> cartShippingFee = shipmentService.getShippingFeeForCart(cartItemIds);
        return ResponseEntity.ok(cartShippingFee);
    }

    @PostMapping("/fee-for-checkout")
    public ResponseEntity<List<CartShippingFee>> getFeeForCheckout(@RequestBody CheckoutShippingFee checkoutShippingFee) {
        List<CartShippingFee> cartShippingFee = shipmentService.getShippingFeeForCheckout(checkoutShippingFee);
        return ResponseEntity.ok(cartShippingFee);
    }

    @PutMapping("/create-shipments")
    public ResponseEntity<?> create(@RequestBody CreateShipmentRequest createShipmentRequest) {
        shipmentService.createShipments(createShipmentRequest);
        orderService.updateStatus(createShipmentRequest.getShopOrderIds(), ShopOrderStatus.PREPARING);
        return ResponseEntity.ok(Map.of("message", "Create shipping order successfully"));
    }

    @PutMapping("create-shipments/{shopOrderId}")
    public ResponseEntity<?> availableForShip(@PathVariable String shopOrderId, @RequestBody @Valid BaseCreateShipmentRequest request) {
        orderService.availableForShipById(shopOrderId, request);
        return ResponseEntity.ok(Map.of("message", "approval order successfully!"));
    }
}