package com.haiemdavang.AnrealShop.tech.kafka.producer;

import com.haiemdavang.AnrealShop.tech.kafka.config.KafkaTopicConfig;
import com.haiemdavang.AnrealShop.tech.kafka.dto.ProductSyncMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductKafkaProducer {
    private final KafkaTemplate<String, ProductSyncMessage> kafkaTemplate;

    public void sendProductSyncMessage(ProductSyncMessage productSyncMessage) {
        if (TransactionSynchronizationManager.isActualTransactionActive()
                && TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    send(productSyncMessage);
                }
            });
            return;
        }

        send(productSyncMessage);
    }

    private void send(ProductSyncMessage productSyncMessage) {
        kafkaTemplate.send(KafkaTopicConfig.PRODUCT_SYNC_TOPIC, productSyncMessage.getId(), productSyncMessage);
    }
}
