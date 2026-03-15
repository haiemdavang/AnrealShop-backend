package com.haiemdavang.AnrealShop.controller;

import com.haiemdavang.AnrealShop.dto.product.ProductReviewDto;
import com.haiemdavang.AnrealShop.dto.review.CreateReviewRequest;
import com.haiemdavang.AnrealShop.dto.review.ReviewListResponse;
import com.haiemdavang.AnrealShop.service.IReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reviews")
public class PreviewController {

    private final IReviewService reviewService;

    @PostMapping
    public ResponseEntity<String> createReview(@Valid @RequestBody CreateReviewRequest request) {
        reviewService.createReview(request);
        return ResponseEntity.ok("SUCCESS");
    }

    @GetMapping("/my-reviews")
    public ResponseEntity<List<ProductReviewDto>> getMyReviews() {
        List<ProductReviewDto> reviews = reviewService.getMyReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ReviewListResponse> getReviewsByProductId(
            @PathVariable String productId,
            @RequestParam(required = false, defaultValue = "10") int size) {
        ReviewListResponse response = reviewService.getReviewsByProductId(productId, size);
        return ResponseEntity.ok(response);
    }
}
