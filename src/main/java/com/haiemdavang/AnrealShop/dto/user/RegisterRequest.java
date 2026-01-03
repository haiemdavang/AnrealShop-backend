package com.haiemdavang.AnrealShop.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "FULLNAME_NOT_BLANK")
        @Size(min = 3, max = 50, message = "FULLNAME_SIZE")
        String fullName,
        
        @NotBlank(message = "EMAIL_NOTBLANK")
        @Email(message = "EMAIL_INVALID")
        String email,
        
        @NotBlank(message = "PASSWORD_NOTBLANK")
        @Size(min = 8, max = 20, message = "PASSWORD_SIZE")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,20}$",
                message = "PASSWORD_PATTERN"
        )
        String password
) {
}
