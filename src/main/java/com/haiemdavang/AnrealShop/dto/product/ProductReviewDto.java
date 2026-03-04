package com.haiemdavang.AnrealShop.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductReviewDto {

    private String id;
    private String userId;
    private String userName;
    private String userAvatarUrl;
    private String productId;
    private String productName;
    private String productImage;
    private String orderItemId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProductMediaDto> mediaList;
}
