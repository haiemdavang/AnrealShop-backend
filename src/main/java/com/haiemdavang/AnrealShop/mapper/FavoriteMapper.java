package com.haiemdavang.AnrealShop.mapper;

import com.haiemdavang.AnrealShop.dto.favorite.FavoriteDto;
import com.haiemdavang.AnrealShop.modal.entity.Favorite;
import com.haiemdavang.AnrealShop.modal.entity.product.Product;
import org.springframework.stereotype.Service;

@Service
public class FavoriteMapper {

    public FavoriteDto toDto(Favorite favorite) {
        if (favorite == null) {
            return null;
        }

        Product product = favorite.getProduct();
        return FavoriteDto.builder()
                .id(favorite.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productThumbnail(product.getThumbnailUrl())
                .productPrice(product.getPrice())
                .productDiscountPrice(product.getDiscountPrice())
                .shopId(product.getShop().getId())
                .shopName(product.getShop().getName())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
