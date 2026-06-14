package com.haiemdavang.AnrealShop.tech.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    public static final String PRODUCT_SYNC_TOPIC = "topic-product-sync";
    public static final String NOTICE_SYNC_TOPIC = "topic-notice-sync";
    public static final String SHIPPING_STATUS_SYNC_TOPIC = "topic-shipping-status-sync";
    public static final String EMAIL_SYNC_TOPIC = "email-status-sync";
    public static final String CHECKOUT_SYNC_TOPIC = "flashsale-checkout-topic";

    @Bean
    public NewTopic productSyncTopic(){
        return TopicBuilder.name(PRODUCT_SYNC_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic noticeSyncTopic(){
        return TopicBuilder.name(NOTICE_SYNC_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic shippingSyncTopic(){
        return TopicBuilder.name(SHIPPING_STATUS_SYNC_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic mailSyncTopic(){
        return TopicBuilder.name(EMAIL_SYNC_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic checkoutSyncTopic(){
        return TopicBuilder.name(CHECKOUT_SYNC_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
