package com.haiemdavang.AnrealShop.service.serviceInter;

import com.haiemdavang.AnrealShop.dto.cart.CartDto;
import com.haiemdavang.AnrealShop.dto.cart.CartItemDto;
import com.haiemdavang.AnrealShop.dto.cart.CartSelectedUpdateDto;
import com.haiemdavang.AnrealShop.modal.entity.cart.CartItem;
import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ICartService {
    int countByUserId(String userId);
    boolean addToCart(CartItemDto cartItemDto);
    void removeFromCart(String cartItemId);
    int clearCart(List<String> cartItemIds);
    List<CartDto> getCartItems();
    Map<Shop, Set<CartItem>> getCartItemsByIdIn(List<String> cartItemIds);
    void updateQuantity(String cartItemId, int quantity);

    void updateSelected(@Valid CartSelectedUpdateDto cartSelectedUpdateDto);
}