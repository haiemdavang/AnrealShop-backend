package com.haiemdavang.AnrealShop.service.serviceImp;

import com.haiemdavang.AnrealShop.dto.cart.CartDto;
import com.haiemdavang.AnrealShop.dto.cart.CartItemDto;
import com.haiemdavang.AnrealShop.dto.cart.CartSelectedUpdateDto;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.mapper.CartMapper;
import com.haiemdavang.AnrealShop.mapper.ShopMapper;
import com.haiemdavang.AnrealShop.modal.entity.cart.Cart;
import com.haiemdavang.AnrealShop.modal.entity.cart.CartItem;
import com.haiemdavang.AnrealShop.modal.entity.product.ProductSku;
import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.tech.redis.service.IRedisService;
import com.haiemdavang.AnrealShop.repository.cart.CartItemRepository;
import com.haiemdavang.AnrealShop.repository.cart.CartRepository;
import com.haiemdavang.AnrealShop.repository.product.ProductSkuRepository;
import com.haiemdavang.AnrealShop.security.SecurityUtils;
import com.haiemdavang.AnrealShop.service.serviceInter.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.haiemdavang.AnrealShop.tech.redis.config.RedisTemplate.PREFIX_CART;

@Service
@RequiredArgsConstructor
public class CartServiceImp implements ICartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductSkuRepository productSkuRepository;
    private final IRedisService redisService;
    private final SecurityUtils securityUtils;
    private final CartMapper cartMapper;
    private final ShopMapper shopMapper;

    @Override
    public int countByUserId(String userId) {
        String key = String.format(PREFIX_CART.getValue(), userId);
        Integer cachedCount = redisService.getValue(key, -1);
        if (cachedCount != -1) return cachedCount;
        int count = cartItemRepository.countByCartUserId(userId);
        redisService.addValue(key, count);
        return count;
    }

    @Override
    @Transactional
    public boolean addToCart(CartItemDto cartItemDto) {
        User currentUser = securityUtils.getCurrentUser();
        String userId = currentUser.getId();

        Cart cart = findOrCreateCart(currentUser);
        ProductSku productSku = productSkuRepository.findById(cartItemDto.getProductSkuId())
                .orElseThrow(() -> new BadRequestException("PRODUCT_SKU_NOT_FOUND"));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductSku().getId().equals(cartItemDto.getProductSkuId()))
                .findFirst();

        boolean isNew = existingItem.isEmpty();
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setPrice(item.getPrice());
            item.setQuantity(item.getQuantity() + cartItemDto.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productSku(productSku)
                    .quantity(cartItemDto.getQuantity())
                    .price(productSku.getPrice())
                    .selected(true)
                    .build();
            cart.addItem(newItem);
        }

        cartRepository.save(cart);
        invalidateCartCache(userId);
        return isNew;
    }

    @Override
    @Transactional
    public void removeFromCart(String cartItemId) {

        User currentUser = securityUtils.getCurrentUser();
        String userId = currentUser.getId();

        Cart cart = findOrCreateCart(currentUser);
        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        if (!removed) {
            throw new BadRequestException("CART_ITEM_NOT_FOUND");
        }

        cartRepository.save(cart);
        invalidateCartCache(userId);
    }

    @Override
    @Transactional
    public int clearCart(List<String> productIds) {
        User currentUser = securityUtils.getCurrentUser();
        String userId = currentUser.getId();

        Cart cart = findOrCreateCart(currentUser);
        Set<CartItem> itemsToRemove = cart.getItems().stream()
                .filter(item -> productIds.contains(item.getId()))
                .collect(Collectors.toSet());

        cart.getItems().removeAll(itemsToRemove);

        cartRepository.save(cart);
        invalidateCartCache(userId);
        return itemsToRemove.size();
    }

    @Override
    public List<CartDto> getCartItems() {
        User currentUser = securityUtils.getCurrentUser();
        Set<CartItem> cartItems = cartItemRepository.findCartItemsByUserId(currentUser.getId());

        Map<Shop, List<CartItem>> cartMap = cartItems.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getProductSku().getProduct().getShop(),
                        Collectors.toList()
                ));

        return cartMap.entrySet().stream()
                .map(entry -> {
                    Shop shop = entry.getKey();
                    List<CartItemDto> sortedItems = entry.getValue().stream()
                            .map(cartMapper::toCartItemDto)
                            .sorted(Comparator.comparing(CartItemDto::isSelected).reversed())
                            .toList();

                    return CartDto.builder()
                            .shop(shopMapper.toBaseShopDto(shop))
                            .items(sortedItems)
                            .build();
                })
                .sorted(Comparator.comparing(
                        (CartDto cart) -> cart.getItems().stream().anyMatch(CartItemDto::isSelected)
                ).reversed())
                .toList();
    }

    @Override
    public Map<Shop, Set<CartItem>> getCartItemsByIdIn(List<String> cartItemIds) {
        Set<CartItem> cartItems = cartItemRepository.findAllByIdIn(cartItemIds);

        return cartItems.stream().collect(
                Collectors.groupingBy(
                        item -> item.getProductSku().getProduct().getShop(),
                        Collectors.toSet()
                )
        );
    }

    @Override
    @Transactional
    public void updateQuantity(String cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findWithProductSkuById(cartItemId)
                .orElseThrow(() -> new BadRequestException("CART_ITEM_NOT_FOUND"));

        if (quantity > cartItem.getProductSku().getQuantity())
            throw new BadRequestException("QUANTITY_EXCEED_PRODUCT_SKU_STOCK");

        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);
    }

    @Override
    public void updateSelected(CartSelectedUpdateDto cartSelectedUpdateDto) {
        Set<CartItem> cartItems = cartItemRepository.findAllByIdIn(cartSelectedUpdateDto.getItemIds());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("CART_ITEM_NOT_FOUND");
        }
        cartItems.forEach(cartItem -> cartItem.setSelected(cartSelectedUpdateDto.isSelected()));
        cartItemRepository.saveAll(cartItems);
    }

    private Cart findOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    private void invalidateCartCache(String userId) {
        String key = String.format(PREFIX_CART.getValue(), userId);
        redisService.del(key);
    }
}