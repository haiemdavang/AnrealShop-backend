package com.haiemdavang.AnrealShop.tech.kafka.dto;
import com.haiemdavang.AnrealShop.modal.enums.PaymentGateway;
import com.haiemdavang.AnrealShop.modal.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutMessage {
    private String trackingId;
    private String userId;
    private String productSkuId;
    private Integer quantity;
    private Long averageRating;
    private String addressId;
    private PaymentType paymentMethod = PaymentType.COD;
    private PaymentGateway paymentGateway = PaymentGateway.CASH_ON_DELIVERY;
    private Long timestamp;
    private String ipAddress;

    public static CheckoutMessage buildCheckoutMessage(String userId, String productId, Integer quantity, Long averageRating, String addressId, PaymentGateway paymentGateway, PaymentType paymentType, String ipAddress) {
        return CheckoutMessage.builder()
                .trackingId(UUID.randomUUID().toString())
                .userId(userId)
                .averageRating(averageRating)
                .productSkuId(productId)
                .addressId(addressId)
                .paymentGateway(paymentGateway)
                .paymentMethod(paymentType)
                .quantity(quantity)
                .timestamp(System.currentTimeMillis())
                .ipAddress(ipAddress)
                .build();
    }
}

