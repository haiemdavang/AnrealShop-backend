package com.haiemdavang.AnrealShop.service.serviceImp;

import com.haiemdavang.AnrealShop.dto.shop.ShopDetailDto;
import com.haiemdavang.AnrealShop.dto.shop.ShopDto;
import com.haiemdavang.AnrealShop.dto.shop.ShopUpdateRequest;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.mapper.ShopMapper;
import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.repository.ShopRepository;
import com.haiemdavang.AnrealShop.repository.user.UserRepository;
import com.haiemdavang.AnrealShop.service.IShopService;
import com.haiemdavang.AnrealShop.tech.redis.config.RedisTemplate;
import com.haiemdavang.AnrealShop.tech.redis.service.IRedisService;
import com.haiemdavang.AnrealShop.utils.ApplicationInitHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ShopServiceImp implements IShopService {
    private final ShopRepository shopRepository;
    private final IRedisService redisService;
    private final UserRepository userRepository;
    private final ShopMapper shopMapper;

    @Override
    public Shop findById(String id) {
        return shopRepository.findById(id)
                .orElseThrow(()-> new BadRequestException("SHOP_NOT_FOUND"));
    }

    @Override
    public Shop findByEmailUser(String email) {
        return shopRepository.findByUserEmail(email)
                .orElseThrow(()-> new BadRequestException("SHOP_NOT_FOUND"));
    }

    @Override
    public boolean isExistByUserId(String userId) {
        String key = String.format(RedisTemplate.PREFIX_HAS_SHOP.getValue(), userId);
        var cachedHasShop = redisService.getValue(key, 0);
        if (cachedHasShop != 0) return true;
        int count = shopRepository.countByUserId(userId);
        redisService.addValue(key, count);
        return count == 1;
    }

    @Override
    public ShopDto findDtoByEmailUser(String username) {
        Shop shop = shopRepository.findByUserEmail(username)
                .orElse(null);

        return shopMapper.toShopDto(shop);
    }

    @Override
    public ShopDto findDtoById(String id) {
        Shop shop = findById(id);
        return shopMapper.toShopDto(shop);
    }

    @Override
    public ShopDetailDto findShopDetailById(String id, boolean isSale) {
        Shop shop = findById(id);
        ShopDetailDto detail = new ShopDetailDto();
        detail.setId(shop.getId());
        detail.setName(shop.getName());
        detail.setAvatarUrl(shop.getAvatarUrl());
        detail.setShopUrl(shop.getUrlSlug());
        if (isSale) {
            detail.setAverageRating(shop.getAverageRating());
            detail.setTotalReviews(shop.getTotalReviews());
            detail.setFollowerCount(shop.getFollowerCount());
            detail.setProductCount(shop.getProductCount());
            detail.setDescription(shop.getDescription());
        }
        return detail;
    }

    @Override
    @Transactional
    public ShopDto registerUser(String username, String shopName) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BadRequestException("USER_NOT_FOUND"));

        Shop shop = shopRepository.findByUserEmail(username)
                .orElse(null);
        if (shop != null)
            throw new BadRequestException("USER_ALREADY_HAS_SHOP");

        Shop newShop = Shop.builder()
                .name(shopName)
                .urlSlug(ApplicationInitHelper.toSlug(shopName))
                .user(user)
                .build();

        shopRepository.save(newShop);
        return shopMapper.toShopDto(newShop);
    }

    @Override
    @Transactional
    public ShopDto updateShop(String username, ShopUpdateRequest request) {
        Shop shop = shopRepository.findByUserEmail(username)
                .orElseThrow(() -> new BadRequestException("SHOP_NOT_FOUND"));

        if (request.getName() != null && !request.getName().isEmpty()) {
            shop.setName(request.getName());
            shop.setUrlSlug(ApplicationInitHelper.toSlug(request.getName()));
        }
        if (request.getDescription() != null) {
            shop.setDescription(request.getDescription());
        }
        if (request.getAvatarUrl() != null) {
            shop.setAvatarUrl(request.getAvatarUrl());
        }
        
        shop = shopRepository.save(shop);
        return shopMapper.toShopDto(shop);
    }
}
