package com.haiemdavang.AnrealShop.controller;

import com.haiemdavang.AnrealShop.dto.favorite.FavoriteDto;
import com.haiemdavang.AnrealShop.dto.favorite.FavoriteRequest;
import com.haiemdavang.AnrealShop.service.IFavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final IFavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<FavoriteDto> addFavorite(@Valid @RequestBody FavoriteRequest request) {
        return ResponseEntity.ok(favoriteService.addFavorite(request.getProductId()));
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Map<String, String>> removeFavorite(@PathVariable String productId) {
        favoriteService.removeFavorite(productId);
        return ResponseEntity.ok(Map.of("message", "Đã xóa sản phẩm khỏi danh sách yêu thích"));
    }

    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Map<String, String>> removeFavoriteById(@PathVariable String favoriteId) {
        favoriteService.removeFavoriteById(favoriteId);
        return ResponseEntity.ok(Map.of("message", "Đã xóa sản phẩm khỏi danh sách yêu thích"));
    }

    @GetMapping
    public ResponseEntity<Page<FavoriteDto>> getMyFavorites(
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(favoriteService.getMyFavorites(pageable));
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(@PathVariable String productId) {
        boolean isFavorite = favoriteService.isFavorite(productId);
        return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> countMyFavorites() {
        return ResponseEntity.ok(Map.of("count", favoriteService.countMyFavorites()));
    }

    @GetMapping("/product-ids")
    public ResponseEntity<Set<String>> getMyFavoriteProductIds() {
        return ResponseEntity.ok(favoriteService.getMyFavoriteProductIds());
    }
}
