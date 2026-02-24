package com.haiemdavang.AnrealShop.service.serviceImp;

import com.haiemdavang.AnrealShop.dto.favorite.FavoriteDto;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.exception.ResourceNotFoundException;
import com.haiemdavang.AnrealShop.mapper.FavoriteMapper;
import com.haiemdavang.AnrealShop.modal.entity.Favorite;
import com.haiemdavang.AnrealShop.modal.entity.product.Product;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.repository.FavoriteRepository;
import com.haiemdavang.AnrealShop.repository.product.ProductRepository;
import com.haiemdavang.AnrealShop.security.SecurityUtils;
import com.haiemdavang.AnrealShop.service.IFavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImp implements IFavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final SecurityUtils securityUtils;
    private final FavoriteMapper favoriteMapper;

    @Override
    @Transactional
    public FavoriteDto addFavorite(String productId) {
        User currentUser = securityUtils.getCurrentUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("PRODUCT_NOT_FOUND"));

        if (favoriteRepository.existsByUserAndProduct(currentUser, product)) {
            throw new BadRequestException("PRODUCT_ALREADY_FAVORITE");
        }

        Favorite favorite = Favorite.builder()
                .user(currentUser)
                .product(product)
                .build();

        favorite = favoriteRepository.save(favorite);

        return favoriteMapper.toDto(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(String productId) {
        User currentUser = securityUtils.getCurrentUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("PRODUCT_NOT_FOUND"));

        if (!favoriteRepository.existsByUserAndProduct(currentUser, product)) {
            throw new ResourceNotFoundException("FAVORITE_NOT_FOUND");
        }

        favoriteRepository.deleteByUserAndProduct(currentUser, product);
    }

    @Override
    @Transactional
    public void removeFavoriteById(String favoriteId) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new ResourceNotFoundException("FAVORITE_NOT_FOUND"));

        User currentUser = securityUtils.getCurrentUser();
        if (!favorite.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("FAVORITE_NOT_BELONG_TO_USER");
        }

        favoriteRepository.delete(favorite);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FavoriteDto> getMyFavorites(Pageable pageable) {
        User currentUser = securityUtils.getCurrentUser();
        Page<Favorite> favorites = favoriteRepository.findByUserOrderByCreatedAtDesc(currentUser, pageable);
        return favorites.map(favoriteMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(String productId) {
        User currentUser = securityUtils.getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("PRODUCT_NOT_FOUND"));
        return favoriteRepository.existsByUserAndProduct(currentUser, product);
    }

    @Override
    @Transactional(readOnly = true)
    public int countMyFavorites() {
        User currentUser = securityUtils.getCurrentUser();
        return favoriteRepository.countByUser(currentUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getMyFavoriteProductIds() {
        User currentUser = securityUtils.getCurrentUser();
        return favoriteRepository.findProductIdsByUser(currentUser);
    }
}
