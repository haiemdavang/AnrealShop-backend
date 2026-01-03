package com.haiemdavang.AnrealShop.dto.auth;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPwRequest {
    @NotBlank(message = "EMAIL_BLANK")
    @Email(message = "EMAIL_INVALID")
    private String email;

    @NotBlank(message = "PASSWORD_BLANK")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
            message = "PASSWORD_INVALID"
    )
    private String password;

    @NotBlank(message = "OTP_BLANK")
    @Pattern(
            regexp = "^[0-9]{6}$",
            message = "OTP_INVALID")
    private String otp;
}