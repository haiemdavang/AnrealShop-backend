package com.haiemdavang.AnrealShop.security.oauth2;

import com.haiemdavang.AnrealShop.dto.auth.Oauth2.OAuth2UserInfo;
import com.haiemdavang.AnrealShop.dto.auth.Oauth2.OAuth2UserInfoFactory;
import com.haiemdavang.AnrealShop.dto.auth.Oauth2.OAuthProvider;
import com.haiemdavang.AnrealShop.exception.AnrealShopException;
import com.haiemdavang.AnrealShop.mapper.UserMapper;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.modal.enums.RoleName;
import com.haiemdavang.AnrealShop.repository.user.UserRepository;
import com.haiemdavang.AnrealShop.security.userDetails.UserDetailSecu;
import com.haiemdavang.AnrealShop.service.IRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final IRoleService roleService;
    private final UserMapper userMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new AnrealShopException("OAUTH2_USER_PROCESSING_ERROR");
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(OAuthProvider.valueOf(registrationId.toUpperCase()), oAuth2User.getAttributes());

        if (StringUtils.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new AnrealShopException("OAUTH2_EMAIL_NOT_FOUND");
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if (userOptional.isEmpty()) {
            user = userMapper.createUserFromOauth2UserInfo(oAuth2UserInfo);
            user.setRole(roleService.getRoleByName(RoleName.USER));
            userRepository.save(user);
        }else {
            user = userOptional.get();
        }


        return UserDetailSecu.createUserDetailFormOAuth2(user, oAuth2User.getAttributes());
    }

}
