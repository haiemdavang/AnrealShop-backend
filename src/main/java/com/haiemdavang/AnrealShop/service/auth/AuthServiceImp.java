package com.haiemdavang.AnrealShop.service.auth;

import com.haiemdavang.AnrealShop.dto.auth.LoginRequest;
import com.haiemdavang.AnrealShop.dto.auth.LoginResponse;
import com.haiemdavang.AnrealShop.dto.user.UserDto;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.security.jwt.JwtInit;
import com.haiemdavang.AnrealShop.service.serviceInter.IAuthService;
import com.haiemdavang.AnrealShop.service.serviceInter.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AuthServiceImp implements IAuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtInit jwtInit;
    private final IUserService userService;
    private final LoginHistoryService loginHistoryService;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            ResponseCookie accessTokenCookie = jwtInit.generaJwtCookie(authentication);
            ResponseCookie refreshTokenCookie = jwtInit.generaJwtRefreshCookie(authentication);

            response.addHeader("Set-Cookie", accessTokenCookie.toString());
            response.addHeader("Set-Cookie", refreshTokenCookie.toString());

            UserDto user = userService.findDtoByEmail(loginRequest.getUsername());

            loginHistoryService.saveLoginHistory(request);

            return new LoginResponse(accessTokenCookie.getValue(), user);
        } catch (Exception e) {
            throw new BadRequestException("LOGIN_FAILED");
        }
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtInit.getTokenFromCookie(request);
        jwtInit.deleteToken(token);

        ResponseCookie accessTokenCookie = jwtInit.getCleanJwtCookie();
        ResponseCookie refreshTokenCookie = jwtInit.getCleanJwtRefreshCookie();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }

    @Override
    public LoginResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtInit.getTokenRefreshFromCookie(request);
        jwtInit.deleteToken(token);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        ResponseCookie newToken = jwtInit.generaJwtCookie(auth);
        ResponseCookie newTokenRefresh = jwtInit.generaJwtRefreshCookie(auth);

        response.addHeader("Set-Cookie", newToken.toString());
        response.addHeader("Set-Cookie", newTokenRefresh.toString());

        return new LoginResponse(newToken.getValue(), null);
    }
}
