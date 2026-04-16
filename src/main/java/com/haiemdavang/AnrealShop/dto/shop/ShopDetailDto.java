package com.haiemdavang.AnrealShop.dto.shop;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShopDetailDto extends ShopDto {
    private String description;
    private Integer productCount;
    private Float averageRating;
    private Integer totalReviews;
    private Integer followerCount;
}

