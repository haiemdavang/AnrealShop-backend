package com.haiemdavang.AnrealShop.mapper;

import com.haiemdavang.AnrealShop.dto.review.ReviewSummaryDto;
import com.haiemdavang.AnrealShop.modal.entity.product.ProductReview;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class PreviewMapper {

    public ReviewSummaryDto buildReviewSummary(Collection<ProductReview> reviews) {
        int total = reviews.size();
        float avg = 0;
        int[] starCounts = new int[6]; // index 1-5

        for (ProductReview r : reviews) {
            int rating = r.getRating();
            if (rating >= 1 && rating <= 5) {
                starCounts[rating]++;
            }
        }

        if (total > 0) {
            avg = (float) reviews.stream().mapToInt(ProductReview::getRating).sum() / total;
        }

        return ReviewSummaryDto.builder()
                .totalReviews(total)
                .averageRating(Math.round(avg * 10) / 10f)
                .fiveStar(starCounts[5])
                .fourStar(starCounts[4])
                .threeStar(starCounts[3])
                .twoStar(starCounts[2])
                .oneStar(starCounts[1])
                .build();
    }
}
