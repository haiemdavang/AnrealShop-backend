package com.haiemdavang.AnrealShop.tech.kafka.consumer;

import com.haiemdavang.AnrealShop.modal.entity.shipping.Shipping;
import com.haiemdavang.AnrealShop.modal.enums.ShippingStatus;
import com.haiemdavang.AnrealShop.modal.enums.ShopOrderStatus;
import com.haiemdavang.AnrealShop.service.serviceInter.IShipmentService;
import com.haiemdavang.AnrealShop.service.order.IShopOrderService;
import com.haiemdavang.AnrealShop.tech.kafka.config.KafkaTopicConfig;
import com.haiemdavang.AnrealShop.tech.kafka.dto.ShippingSyncMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingStatusKafkaConsumer {
    private final IShipmentService shipmentService;
    private final IShopOrderService orderService;

    @KafkaListener(topics = KafkaTopicConfig.SHIPPING_STATUS_SYNC_TOPIC)
    public void listen(ShippingSyncMessage message) {
        log.info("Received Shipping Sync Message: {}", message);
        Shipping shipping = shipmentService.processShippingSyncMessage(message);
        if (message.getStatus().equals(ShippingStatus.DELIVERED)) {
            orderService.updateStatus(Collections.singletonList(shipping.getShopOrder().getId()), ShopOrderStatus.DELIVERED);
        }
    }
}
