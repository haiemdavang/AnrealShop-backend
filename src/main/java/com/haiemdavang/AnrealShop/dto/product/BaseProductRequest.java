package com.haiemdavang.AnrealShop.dto.product;

import com.haiemdavang.AnrealShop.dto.attribute.ProductAttributeDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseProductRequest {
    @NotBlank(message = "PRODUCT_NAME_NOTBLANK")
    @Size(min = 5, max = 255, message = "PRODUCT_NAME_SIZE")
    private String name;

    @NotBlank(message = "PRODUCT_DESCRIPTION_NOTBLANK")
    private String description;

    @NotBlank(message = "PRODUCT_SORT_DESCRIPTION_NOTBLANK")
    private String sortDescription;

    @NotNull(message = "PRODUCT_PRICE_NOTNULL")
    @Min(value = 1000, message = "PRODUCT_PRICE_MIN")
    private Long price;

    @NotNull(message = "PRODUCT_PRICE_NOTNULL")
    @Min(value = 1000, message = "PRODUCT_PRICE_MIN")
    private Long discountPrice;

    @NotNull(message = "PRODUCT_QUANTITY_NOTNULL")
    @Min(value = 0, message = "PRODUCT_QUANTITY_MIN")
    private Integer quantity;

    @NotNull(message = "PRODUCT_CATEGORYID_NOTNULL")
    private String categoryId;

    private String categoryPath;

    private String categorySlug;

    @NotNull(message = "PRODUCT_WEIGHT_NOTNULL")
    private Long weight;

    @NotNull(message = "PRODUCT_HEIGHT_NOTNULL")
    private Long height;

    @NotNull(message = "PRODUCT_LENGTH_NOTNULL")
    private Long length;

    @NotNull(message = "PRODUCT_WIDTH_NOTNULL")
    private Long width;

    private List<BaseProductSkuRequest> productSkus;

    private List<ProductAttributeDto> attributes;

    private List<ProductMediaDto> media;
}
