package com.haiemdavang.AnrealShop.dto.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.haiemdavang.AnrealShop.dto.product.ProductReviewDto;
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
public class ReviewListResponse {
    private ReviewSummaryDto summary;
    private List<ProductReviewDto> reviews;
}
