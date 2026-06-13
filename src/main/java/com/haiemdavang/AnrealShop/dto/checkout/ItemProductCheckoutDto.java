package com.haiemdavang.AnrealShop.dto.checkout;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemProductCheckoutDto {
    private String productSkuId;
    private int quantity;
}
