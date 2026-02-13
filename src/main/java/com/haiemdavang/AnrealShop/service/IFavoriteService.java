package com.haiemdavang.AnrealShop.service;

import com.haiemdavang.AnrealShop.dto.favorite.FavoriteDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface IFavoriteService {

    FavoriteDto addFavorite(String productId);

    void removeFavorite(String productId);

    void removeFavoriteById(String favoriteId);

    Page<FavoriteDto> getMyFavorites(Pageable pageable);

    boolean isFavorite(String productId);

    int countMyFavorites();

    Set<String> getMyFavoriteProductIds();
}
