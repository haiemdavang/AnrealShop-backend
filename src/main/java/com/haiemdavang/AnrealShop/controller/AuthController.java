package com.haiemdavang.AnrealShop.controller;

import com.haiemdavang.AnrealShop.dto.auth.HistoryLoginDto;
import com.haiemdavang.AnrealShop.dto.auth.LoginRequest;
import com.haiemdavang.AnrealShop.dto.auth.LoginResponse;
import com.haiemdavang.AnrealShop.dto.auth.ForgotPwRequest;
import com.haiemdavang.AnrealShop.service.auth.LoginHistoryService;
import com.haiemdavang.AnrealShop.tech.mail.service.IMailService;
import com.haiemdavang.AnrealShop.service.serviceInter.IAuthService;
import com.haiemdavang.AnrealShop.service.serviceInter.IUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthController {

    private final IAuthService authService;
    private final IMailService mailService;
    private final IUserService userService;
    private final LoginHistoryService loginHistoryService;
    
    @PostMapping("/login")
    public ResponseEntity< LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response, HttpServletRequest httpServletRequest){
        return ResponseEntity.ok(authService.login(request, response, httpServletRequest));
    }


    @PostMapping("auth/refresh-token")
    public ResponseEntity< LoginResponse> refreshToken(HttpServletRequest request, HttpServletResponse response){
        return ResponseEntity.ok(authService.refreshToken(request, response));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công!"));
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<?> resetPass(@RequestBody @Valid ForgotPwRequest resetPassword){
        mailService.verifyOTP(resetPassword.getOtp(), resetPassword.getEmail());
        userService.resetPassword(resetPassword.getEmail(), resetPassword.getPassword());
        mailService.delOTP(resetPassword.getEmail());
        return ResponseEntity.ok(Map.of("message", "Thay đổi mật khẩu thành công!"));
    }

    @GetMapping("/history-login")
    public ResponseEntity<List<HistoryLoginDto>> getLoginHistory(HttpServletRequest request) {
        return ResponseEntity.ok(loginHistoryService.getLoginHistory(request));
    }

}
