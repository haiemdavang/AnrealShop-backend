package com.haiemdavang.AnrealShop.mapper;

import com.haiemdavang.AnrealShop.dto.checkout.CheckoutRequestDto;
import com.haiemdavang.AnrealShop.dto.checkout.ItemProductCheckoutDto;
import com.haiemdavang.AnrealShop.tech.kafka.dto.CheckoutMessage;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CheckoutMapper {

    public static List<CheckoutMessage> toCheckoutMessages(
            String userId,
            Long averageRating,
            CheckoutRequestDto request,
            String ipAddress
    ) {

        if (request == null || request.getItems() == null) {
            return Collections.emptyList();
        }

        return request.getItems().stream()
                .filter(Objects::nonNull)
                .map(item -> CheckoutMessage.buildCheckoutMessage(
                        userId,
                        item.getProductSkuId(),
                        item.getQuantity(),
                        averageRating,
                        request.getAddressId(),
                        request.getPaymentGateway(),
                        request.getPaymentMethod(),
                        ipAddress
                ))
                .collect(Collectors.toList());
    }

    public static CheckoutRequestDto toCheckoutRequest(
            CheckoutMessage message
    ) {

        if (message == null) {
            return null;
        }

        ItemProductCheckoutDto item =
                ItemProductCheckoutDto.builder()
                        .productSkuId(message.getProductSkuId())
                        .quantity(message.getQuantity())
                        .build();

        return CheckoutRequestDto.builder()
                .userId(message.getUserId())
                .addressId(message.getAddressId())
                .paymentMethod(message.getPaymentMethod())
                .paymentGateway(message.getPaymentGateway())
                .ipAddress(message.getIpAddress())
                .items(List.of(item))
                .build();
    }
}
