package com.haiemdavang.AnrealShop.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.haiemdavang.AnrealShop.dto.attribute.ProductAttributeDto;
import com.haiemdavang.AnrealShop.dto.review.ReviewSummaryDto;
import com.haiemdavang.AnrealShop.dto.shop.BaseShopDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDetailDto {
    private String id;
    private String name;
    private String thumbnailUrl;
    private String urlSlug;
    private String categoryId; // nullable = true
    private String categoryPath; // nullable = true
    private String description; // nullable = true
    private String sortDescription; // nullable = true
    private Long price; // nullable = true
    private Long discountPrice;
    private Integer quantity;
    private Integer sold;
    private String status;
    private Boolean visible;

    private String createdAt;
    private String updatedAt; // nullable = true

    private String restrictedReason; // nullable = true
    private boolean isRestricted; // nullable = true
    private String restrictStatus; // nullable = true

    private float averageRating; // nullable = true
    private int totalReviews; // nullable = true

    private Long weight; // nullable = true
    private Long height; // nullable = true
    private Long length; // nullable = true
    private Long width; // nullable = true

    private BaseShopDto baseShopDto; // nullable = true
    private List<ProductMediaDto> medias; // nullable = true
    private List<ProductAttributeDto> attributes; // nullable = true
    private List<MyShopProductSkuDto> productSkus; // nullable = true
    private List<ProductReviewDto> reviews; // nullable = true
    private ReviewSummaryDto reviewSummary; // nullable = true

}