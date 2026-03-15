package com.haiemdavang.AnrealShop.tech.kafka.producer;

import com.haiemdavang.AnrealShop.tech.kafka.config.KafkaTopicConfig;
import com.haiemdavang.AnrealShop.tech.kafka.dto.ShippingSyncMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.haiemdavang.AnrealShop.tech.kafka.config.KafkaTopicConfig.SHIPPING_STATUS_SYNC_TOPIC;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingStatusKafkaProducer {
    private final KafkaTemplate<String, ShippingSyncMessage> kafkaTemplate;

    public void sendSyncMessage(ShippingSyncMessage shippingSyncMessage) {
        kafkaTemplate.send(SHIPPING_STATUS_SYNC_TOPIC, shippingSyncMessage.getId(), shippingSyncMessage);
    }

}
