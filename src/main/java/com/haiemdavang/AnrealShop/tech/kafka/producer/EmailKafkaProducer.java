package com.haiemdavang.AnrealShop.tech.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haiemdavang.AnrealShop.tech.kafka.dto.EmailMessageDto;
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
public class EmailKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendEmailSyncMessage(EmailMessageDto emailMessageDto) {
        try {
            String json = objectMapper.writeValueAsString(emailMessageDto);
            kafkaTemplate.send(KafkaTopicConfig.EMAIL_SYNC_TOPIC, UUID.randomUUID().toString(), json);
        } catch (Exception e) {
            log.error("Error converting EmailMessageDto to JSON", e);
            throw new AnrealShopException("CONVERT_TO_JSON_ERROR");
        }
    }
}

