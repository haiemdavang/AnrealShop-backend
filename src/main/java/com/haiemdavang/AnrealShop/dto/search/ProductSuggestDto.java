package com.haiemdavang.AnrealShop.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSuggestDto {
    private String id;
    private String name;
    private String urlSlug;
    private Long price;
    private Long discountPrice;
    private String thumbnailUrl;
    private String categoryName;
}
