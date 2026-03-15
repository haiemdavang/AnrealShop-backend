package com.haiemdavang.AnrealShop.dto.review;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewSummaryDto {
    private int totalReviews;
    private float averageRating;
    private int fiveStar;
    private int fourStar;
    private int threeStar;
    private int twoStar;
    private int oneStar;
}
