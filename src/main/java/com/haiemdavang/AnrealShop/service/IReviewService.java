package com.haiemdavang.AnrealShop.service;

import com.haiemdavang.AnrealShop.dto.product.ProductReviewDto;
import com.haiemdavang.AnrealShop.dto.review.CreateReviewRequest;
import com.haiemdavang.AnrealShop.dto.review.ReviewListResponse;
import jakarta.validation.Valid;

public interface IReviewService {

    ProductReviewDto createReview(@Valid CreateReviewRequest request);

    ReviewListResponse getReviewsByProductId(String productId, int size);
}
