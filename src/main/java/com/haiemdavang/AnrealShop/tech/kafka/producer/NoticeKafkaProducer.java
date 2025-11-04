package com.haiemdavang.AnrealShop.tech.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haiemdavang.AnrealShop.dto.notice.SimpleNoticeMessage;
import com.haiemdavang.AnrealShop.exception.AnrealShopException;
import com.haiemdavang.AnrealShop.tech.kafka.config.KafkaTopicConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendNoticeSyncMessage(SimpleNoticeMessage simpleNoticeMessage) {
        try {
            String json = objectMapper.writeValueAsString(simpleNoticeMessage);
            kafkaTemplate.send(KafkaTopicConfig.NOTICE_SYNC_TOPIC, UUID.randomUUID().toString(), json);
        } catch (Exception e) {
            throw new AnrealShopException("CONVERT_TO_JSON_ERROR");
        }
    }
}
