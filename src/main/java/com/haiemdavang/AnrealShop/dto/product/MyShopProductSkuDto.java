package com.haiemdavang.AnrealShop.dto.product;

import com.haiemdavang.AnrealShop.dto.attribute.ProductAttributeSingleValueDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyShopProductSkuDto {
    private String id;
    private String sku;
    private String imageUrl;
    private Long price;
    private Integer quantity;
    private Integer sold;
    private String createdAt;
    private String[] keyAttributes;
    private List<ProductAttributeSingleValueDto> attributeForSku;
}