package com.haiemdavang.AnrealShop.dto.kyc;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanIdRequest {

    @NotBlank(message = "IMAGE_BASE64_REQUIRED")
    private String imageBase64;
}
