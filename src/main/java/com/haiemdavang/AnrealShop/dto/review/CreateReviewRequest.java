package com.haiemdavang.AnrealShop.dto.review;

import com.haiemdavang.AnrealShop.dto.product.ProductMediaDto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {

    @NotBlank(message = "ORDER_ITEM_NOT_BLANK")
    private String orderItemId;

    @NotNull(message = "RATING_NOT_NULL")
    @Min(value = 1, message = "RATING_MIN_1")
    @Max(value = 5, message = "RATING_MAX_5")
    private Integer rating;

    private String comment;

    private List<ProductMediaDto> mediaList;
}
