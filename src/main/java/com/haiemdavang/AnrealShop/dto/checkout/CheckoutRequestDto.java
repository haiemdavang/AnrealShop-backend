package com.haiemdavang.AnrealShop.dto.checkout;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.haiemdavang.AnrealShop.modal.enums.PaymentGateway;
import com.haiemdavang.AnrealShop.modal.enums.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class CheckoutRequestDto {
    @NotNull
    @NotBlank(message = "ADDRESS_ID_NOT_BLANK")
    private String addressId;
    @Builder.Default
    private PaymentType paymentMethod = PaymentType.COD;
    @Builder.Default
    private PaymentGateway paymentGateway = PaymentGateway.CASH_ON_DELIVERY;
    private String ipAddress;
    private String userId;

    private List<ItemProductCheckoutDto> items;
}
