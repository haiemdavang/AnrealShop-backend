package com.haiemdavang.AnrealShop.dto.auth;

import com.haiemdavang.AnrealShop.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String accessToken;
    private UserDto user;
}
