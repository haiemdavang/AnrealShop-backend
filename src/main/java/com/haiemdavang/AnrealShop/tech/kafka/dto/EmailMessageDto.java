package com.haiemdavang.AnrealShop.tech.kafka.dto;

import com.haiemdavang.AnrealShop.dto.order.ProductOrderItemDto;
import com.haiemdavang.AnrealShop.tech.mail.MailType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Set<ProductOrderItemDto> items;
    private boolean isForShop;

    public static EmailMessageDto buildForNewOrder(String orderId) {
        return EmailMessageDto.builder()
                .code(orderId)
                .mailType(MailType.NEW_ORDER)
                .build();
    }
}

