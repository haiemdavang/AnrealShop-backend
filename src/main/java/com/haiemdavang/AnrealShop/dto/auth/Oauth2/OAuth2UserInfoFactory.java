package com.haiemdavang.AnrealShop.dto.auth.Oauth2;

import com.haiemdavang.AnrealShop.exception.AnrealShopException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(OAuthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            default -> throw new AnrealShopException("Provider " + provider + " is not supported yet.");
        };
    }
}
