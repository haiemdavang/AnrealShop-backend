package com.haiemdavang.AnrealShop.tech.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haiemdavang.AnrealShop.tech.kafka.dto.notice.SimpleNoticeMessage;
import com.haiemdavang.AnrealShop.service.notice.NotificationService;
import com.haiemdavang.AnrealShop.tech.kafka.config.KafkaTopicConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeKafkaConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopicConfig.NOTICE_SYNC_TOPIC, groupId = "order-service")
    public void consumeOrderEvent(String message) {
        try {
            notificationService.sendMessage(objectMapper.readValue(message, SimpleNoticeMessage.class));
        } catch (Exception e) {
            log.error("Error parsing notification: {}", message, e);
        }
    }
}
