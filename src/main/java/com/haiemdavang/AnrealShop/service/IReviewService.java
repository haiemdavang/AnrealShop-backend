package com.haiemdavang.AnrealShop.service;

import com.haiemdavang.AnrealShop.dto.review.CreateReviewRequest;
import com.haiemdavang.AnrealShop.dto.review.ReviewListResponse;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.Set;

public interface IReviewService {

    void createReview(@Valid CreateReviewRequest request);

    ReviewListResponse getReviewsByProductId(String productId, int size);

    Set<String> getReviewedOrderItemIds(Collection<String> orderItemIds);
}
