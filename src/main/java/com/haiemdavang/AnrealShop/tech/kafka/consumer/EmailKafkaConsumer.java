package com.haiemdavang.AnrealShop.tech.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haiemdavang.AnrealShop.tech.kafka.config.KafkaTopicConfig;
import com.haiemdavang.AnrealShop.tech.kafka.dto.EmailMessageDto;
import com.haiemdavang.AnrealShop.tech.mail.service.MailServiceImp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailKafkaConsumer {

    private final MailServiceImp mailServiceImp;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopicConfig.EMAIL_SYNC_TOPIC, groupId = "email-service-group")
    public void consumeOrderEvent(String message) {
        try {
            EmailMessageDto emailMessageDto = objectMapper.readValue(message, EmailMessageDto.class);
            switch (emailMessageDto.getMailType()) {
                case NEW_ORDER:
                    mailServiceImp.sendMailNewOrder(emailMessageDto.getCode());
                    break;
                case ORDER_SHIPPING:
                    mailServiceImp.sendMailShipperPickup(emailMessageDto.getShopOrderIds());
                    break;
                case ORDER_DELIVERING:
                    mailServiceImp.sendMailOrderDelivering(emailMessageDto.getCode());
                    break;
                default:
                    log.warn("Received email message with unknown mail type: {}", emailMessageDto.getMailType());
            }
        } catch (Exception e) {
            log.error("Error parsing notification: {}", message, e);
        }
    }
}
