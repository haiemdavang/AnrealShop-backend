package com.haiemdavang.AnrealShop.tech.kafka.producer;

import com.haiemdavang.AnrealShop.tech.kafka.config.KafkaTopicConfig;
import com.haiemdavang.AnrealShop.tech.kafka.dto.CheckoutMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaCheckoutProducer {
    private final KafkaTemplate<String, CheckoutMessage> kafkaTemplate;
    public void sendPurchaseRequest(CheckoutMessage message) {
        kafkaTemplate.send(KafkaTopicConfig.CHECKOUT_SYNC_TOPIC, message.getProductSkuId(), message);
    }
}
