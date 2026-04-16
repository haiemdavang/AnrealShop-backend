package com.haiemdavang.AnrealShop.controller;

import com.haiemdavang.AnrealShop.dto.shop.ShopDto;
import com.haiemdavang.AnrealShop.service.IShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/public/shops")
public class PublicShopController {
    
    private final IShopService shopService;

    @GetMapping("/{id}")
    public ResponseEntity<? extends ShopDto> getShopDetails(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "false") boolean isSale) {
        if (isSale) {
            return ResponseEntity.ok(shopService.findShopDetailById(id, true));
        }
        return ResponseEntity.ok(shopService.findDtoById(id));
    }

}
