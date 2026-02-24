package com.haiemdavang.AnrealShop.dto.favorite;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteRequest {
    @NotBlank(message = "PRODUCT_ID_REQUIRED")
    private String productId;
}
