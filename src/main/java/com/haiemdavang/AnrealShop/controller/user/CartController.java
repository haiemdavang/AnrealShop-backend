package com.haiemdavang.AnrealShop.controller.user;

import com.haiemdavang.AnrealShop.dto.cart.CartItemDto;
import com.haiemdavang.AnrealShop.dto.cart.CartSelectedUpdateDto;
import com.haiemdavang.AnrealShop.service.serviceInter.ICartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/cart")
public class CartController {

    private final ICartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody @Valid CartItemDto cartItemDto) {
        boolean isNew = cartService.addToCart(cartItemDto);
        return ResponseEntity.ok(Map.of("message","Product is added to cart", "isNew", isNew ));
    }

    @PutMapping("/update-selected")
    public ResponseEntity<?> updateSelectedItem(@RequestBody @Valid CartSelectedUpdateDto cartSelectedUpdateDto) {
        cartService.updateSelected(cartSelectedUpdateDto);
        return ResponseEntity.ok(Map.of("message","Product is update selected to cart"));
    }

    @PutMapping("/update-quantity")
    public ResponseEntity<?> updateQuantity(@RequestParam String cartItemId, @RequestParam int quantity) {
        cartService.updateQuantity(cartItemId, quantity);
        return ResponseEntity.ok(Map.of("message","Quantity is update to cart"));
    }

    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<?> removeFromCart(@PathVariable String cartItemId) {
        cartService.removeFromCart(cartItemId);
        return ResponseEntity.ok(Map.of("message","Product is remove to cart" ));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(@RequestBody List<String> cartItemIds) {
        return ResponseEntity.ok(Map.of("countDelete", cartService.clearCart(cartItemIds)));
    }

    @GetMapping
    public ResponseEntity<?> getCart() {
        return ResponseEntity.ok(cartService.getCartItems());
    }
}