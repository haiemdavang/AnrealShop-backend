package com.haiemdavang.AnrealShop.controller.user;

import com.haiemdavang.AnrealShop.dto.checkout.CheckoutInfoDto;
import com.haiemdavang.AnrealShop.dto.checkout.CheckoutRequestDto;
import com.haiemdavang.AnrealShop.dto.checkout.CheckoutResponseDto;
import com.haiemdavang.AnrealShop.mapper.CheckoutMapper;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.security.SecurityUtils;
import com.haiemdavang.AnrealShop.service.serviceInter.ICheckoutService;
import com.haiemdavang.AnrealShop.tech.kafka.dto.CheckoutMessage;
import com.haiemdavang.AnrealShop.tech.kafka.producer.KafkaCheckoutProducer;
import com.haiemdavang.AnrealShop.tech.poolRequest.SseService;
import com.haiemdavang.AnrealShop.utils.ApplicationInitHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/checkout")
public class CheckoutController {
    private final ICheckoutService checkoutService;
    private final SseService sseService;
    private final KafkaCheckoutProducer kafkaCheckoutProducer;
    private final SecurityUtils securityUtil;

    @PostMapping("/items")
    public ResponseEntity<List<CheckoutInfoDto>> getListCheckout(@RequestBody Map<String, Integer> itemsCheckoutRequest) {
        return ResponseEntity.ok(checkoutService.getListCheckout(itemsCheckoutRequest));
    }

    @PostMapping
    public ResponseEntity<CheckoutResponseDto> checkout(@RequestBody @Valid CheckoutRequestDto requestDto, HttpServletRequest request) {
        User currentUser = securityUtil.getCurrentUser();
        checkoutService.validateItems(requestDto.getItems());
        requestDto.setUserId(currentUser.getId());
        return ResponseEntity.ok(checkoutService.DecreaseBeforeCheckout(requestDto));
    }

    @PostMapping("/polling")
    public ResponseEntity<Map<String, String>> submitCheckout(@RequestBody @Valid CheckoutRequestDto requestDto, HttpServletRequest request) {
        checkoutService.validateItems(requestDto.getItems());
        User currentUser = securityUtil.getCurrentUser();
        CheckoutMessage checkoutMessage = CheckoutMapper.toCheckoutMessages(
                currentUser.getId(),
                currentUser.getAverageRating(),
                requestDto,
                ApplicationInitHelper.getClientIpAddress(request)
        ).get(0);
        kafkaCheckoutProducer.sendPurchaseRequest(checkoutMessage);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "message", "Yêu cầu đang được xử lý",
                        "trackingId", checkoutMessage.getTrackingId()
                ));
    }

    @GetMapping(value = "/stream/{trackingId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamCheckoutResult(@PathVariable String trackingId) {
        return sseService.createEmitter(trackingId);
    }

}
