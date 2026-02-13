package com.haiemdavang.AnrealShop.dto.favorite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDto {
    private String id;
    private String productId;
    private String productName;
    private String productThumbnail;
    private Long productPrice;
    private Long productDiscountPrice;
    private String shopId;
    private String shopName;
    private LocalDateTime createdAt;
}
