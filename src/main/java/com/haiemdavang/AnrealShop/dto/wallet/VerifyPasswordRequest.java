package com.haiemdavang.AnrealShop.dto.wallet;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyPasswordRequest {

    @NotBlank(message = "PAYMENT_PASSWORD_REQUIRED")
    private String paymentPassword;
}
