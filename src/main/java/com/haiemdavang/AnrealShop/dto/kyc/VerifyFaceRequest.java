package com.haiemdavang.AnrealShop.dto.kyc;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyFaceRequest {

    @NotBlank(message = "ID_IMAGE_BASE64_REQUIRED")
    private String idImageBase64;

    @NotBlank(message = "SELFIE_IMAGE_BASE64_REQUIRED")
    private String selfieImageBase64;
}
