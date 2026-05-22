package com.haiemdavang.AnrealShop.service.serviceInter;

import com.haiemdavang.AnrealShop.dto.auth.LoginRequest;
import com.haiemdavang.AnrealShop.dto.auth.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {
    LoginResponse login(LoginRequest loginRequest, HttpServletResponse response, HttpServletRequest request);
    void logout(HttpServletRequest request, HttpServletResponse response);
    LoginResponse refreshToken(HttpServletRequest request, HttpServletResponse response);
}
