package com.haiemdavang.AnrealShop.dto.auth;

public record OtpRequest(
        String code,
        String email
) {
}
