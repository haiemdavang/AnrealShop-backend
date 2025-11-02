package com.haiemdavang.AnrealShop.security;

import com.haiemdavang.AnrealShop.exception.ForbiddenException;
import com.haiemdavang.AnrealShop.exception.UnAuthException;
import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.repository.ShopRepository;
import com.haiemdavang.AnrealShop.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityUtils")
@RequiredArgsConstructor
public class SecurityUtils {
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UnAuthException("USER_NOT_FOUND"));
    }
    
    public Shop getCurrentUserShop() {
        User currentUser = getCurrentUser();
        return shopRepository.findByUser(currentUser)
                .orElseThrow(() -> new ForbiddenException("SHOP_NOT_FOUND_FOR_USER"));
    }
    
    public boolean isCurrentUser(String userId) {
        User currentUser = getCurrentUser();
        return currentUser.getId().equals(userId);
    }

    public boolean isCurrentUserByEmail(String email) {
        User currentUser = getCurrentUser();
        return currentUser.getEmail().equals(email);
    }
    
    public boolean isCurrentUserShop(String shopId) {
        Shop currentUserShop = getCurrentUserShop();
        return currentUserShop.getId().equals(shopId);
    }
    
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }


}
