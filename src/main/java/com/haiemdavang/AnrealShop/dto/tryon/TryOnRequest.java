package com.haiemdavang.AnrealShop.dto.tryon;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TryOnRequest {

    @NotBlank(message = "PERSON_IMAGE_REQUIRED")
    private String personImageBase64;

    @NotBlank(message = "PRODUCT_IMAGE_REQUIRED")
    private String productImageBase64;

    @Builder.Default
    private Integer baseSteps = 25;
}
