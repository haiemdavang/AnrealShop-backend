package com.haiemdavang.AnrealShop.service;


import com.haiemdavang.AnrealShop.dto.shop.ShopDetailDto;
import com.haiemdavang.AnrealShop.dto.shop.ShopDto;
import com.haiemdavang.AnrealShop.dto.shop.ShopUpdateRequest;
import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import jakarta.validation.Valid;

public interface IShopService {
    Shop findById(String id);

    Shop findByEmailUser(String email);

    boolean isExistByUserId(String id);

    ShopDto findDtoByEmailUser(String username);

    ShopDto findDtoById(String id);

    ShopDetailDto findShopDetailById(String id, boolean isSale);

    ShopDto registerUser(String username, @Valid String shopName);

    ShopDto updateShop(String username, @Valid ShopUpdateRequest request);
}
