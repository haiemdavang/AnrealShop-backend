package com.haiemdavang.AnrealShop.controller.myshop;

import com.haiemdavang.AnrealShop.dto.shop.ShopCreateRequest;
import com.haiemdavang.AnrealShop.dto.shop.ShopDto;
import com.haiemdavang.AnrealShop.dto.user.ProfileRequest;
import com.haiemdavang.AnrealShop.dto.user.RegisterRequest;
import com.haiemdavang.AnrealShop.dto.user.UserDto;
import com.haiemdavang.AnrealShop.security.userDetails.UserDetailSecu;
import com.haiemdavang.AnrealShop.service.IShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/shops")
public class ShopController {
    private final IShopService shopService;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<ShopDto> register(@AuthenticationPrincipal UserDetailSecu userDetails, @Valid @RequestBody ShopCreateRequest shopName) {
        return ResponseEntity.ok(shopService.registerUser(userDetails.getUsername(), shopName.getName()));
    }

    @GetMapping("")
    public ResponseEntity<ShopDto> getCurrentShop(@AuthenticationPrincipal UserDetailSecu userDetails) {
        return ResponseEntity.ok(shopService.findDtoByEmailUser(userDetails.getUsername()));
    }

    @PutMapping("")
    @Transactional
    public ResponseEntity<ShopDto> updateShop(@AuthenticationPrincipal UserDetailSecu userDetails, @Valid @RequestBody com.haiemdavang.AnrealShop.dto.shop.ShopUpdateRequest request) {
        return ResponseEntity.ok(shopService.updateShop(userDetails.getUsername(), request));
    }

}
