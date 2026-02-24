package com.haiemdavang.AnrealShop.mapper;

import com.haiemdavang.AnrealShop.dto.search.CategorySuggestDto;
import com.haiemdavang.AnrealShop.dto.search.ProductSuggestDto;
import com.haiemdavang.AnrealShop.tech.elasticsearch.document.EsCategory;
import com.haiemdavang.AnrealShop.tech.elasticsearch.document.EsProduct;
import org.springframework.stereotype.Component;

@Component
public class PublicSearchMapper {

    public ProductSuggestDto toProductSuggestDto(EsProduct esProduct, String categoryName) {
        return ProductSuggestDto.builder()
                .id(esProduct.getId())
                .name(esProduct.getName())
                .urlSlug(esProduct.getUrlSlug())
                .price(esProduct.getPrice())
                .discountPrice(esProduct.getDiscountPrice())
                .thumbnailUrl(esProduct.getThumbnailUrl())
                .categoryName(categoryName)
                .build();
    }

    public CategorySuggestDto toCategorySuggestDto(EsCategory esCategory) {
        return CategorySuggestDto.builder()
                .id(esCategory.getId())
                .name(esCategory.getName())
                .urlPath(esCategory.getUrlPath())
                .urlSlug(esCategory.getUrlSlug())
                .build();
    }
}
