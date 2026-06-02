package com.haiemdavang.AnrealShop.tech.kafka.dto;

import com.haiemdavang.AnrealShop.dto.order.ProductOrderItemDto;
import com.haiemdavang.AnrealShop.tech.mail.MailType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessageDto {
    private String to;
    private String userName;
    private MailType mailType;
    private String subject;
    private String code;
    private Set<String> shopOrderIds;
    private Set<ProductOrderItemDto> items;
    private boolean isForShop;

    public static EmailMessageDto buildForNewOrder(String orderId) {
        return EmailMessageDto.builder()
                .code(orderId)
                .mailType(MailType.NEW_ORDER)
                .build();
    }

    public static EmailMessageDto buildMailOrderPickedUp(Set<String> shopOrderIds) {
        return EmailMessageDto.builder()
                .shopOrderIds(shopOrderIds)
                .mailType(MailType.ORDER_SHIPPING)
                .build();
    }

    public static EmailMessageDto buildMailOrderOutForDelivery(String shopOrderId) {
        return EmailMessageDto.builder()
                .code(shopOrderId)
                .mailType(MailType.ORDER_DELIVERING)
                .build();
    }
}

