package com.haiemdavang.AnrealShop.tech.kafka.consumer;
import com.haiemdavang.AnrealShop.dto.checkout.CheckoutResponseDto;
import com.haiemdavang.AnrealShop.mapper.CheckoutMapper;
import com.haiemdavang.AnrealShop.service.order.InventoryService;
import com.haiemdavang.AnrealShop.service.serviceImp.CheckoutServiceImp;
import com.haiemdavang.AnrealShop.tech.kafka.config.KafkaTopicConfig;
import com.haiemdavang.AnrealShop.tech.kafka.dto.CheckoutMessage;
import com.haiemdavang.AnrealShop.tech.poolRequest.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckoutConsumer {

    private final InventoryService inventoryService;
    private final SseService sseService;
    private final CheckoutServiceImp checkoutServiceImp;

    @KafkaListener(topics = KafkaTopicConfig.CHECKOUT_SYNC_TOPIC, groupId = "checkout-group")
    public void processCheckoutBatch(List<CheckoutMessage> messages) {
        log.info("Nhận được lô gồm {} requests mua hàng", messages.size());
        Map<String, List<CheckoutMessage>> groupedByProduct = messages.stream()
                .collect(Collectors.groupingBy(CheckoutMessage::getProductSkuId));

        for (Map.Entry<String, List<CheckoutMessage>> entry : groupedByProduct.entrySet()) {
            String productSkuId = entry.getKey();
            List<CheckoutMessage> productRequests = entry.getValue();

            productRequests.sort(Comparator
                    .comparing(CheckoutMessage::getAverageRating).reversed()
                    .thenComparing(CheckoutMessage::getTimestamp)
            );

            for (CheckoutMessage request : productRequests) {
                String trackingId = request.getTrackingId();

                try {
                    boolean hasInventory = inventoryService.deductInventory(productSkuId, request.getQuantity());

                    if (hasInventory) {
                        CheckoutResponseDto rs = checkoutServiceImp.checkout(CheckoutMapper.toCheckoutRequest(request));
                        sseService.sendResult(trackingId, Map.of(
                                "status", "SUCCESS",
                                "message", "Mua hàng thành công!",
                                "orderId", rs.getOrderId()
                        ));
                    } else {
                        sseService.sendResult(trackingId, Map.of(
                                "status", "FAILED",
                                "message", "Đã hết hàng"
                        ));
                    }
                } catch (Exception e) {
                    log.error("Lỗi khi xử lý đơn hàng: {}", trackingId, e);
                    sseService.sendResult(trackingId, Map.of(
                            "status", "ERROR",
                            "message", "Có lỗi xảy ra"
                    ));
                }
            }
        }
    }
}