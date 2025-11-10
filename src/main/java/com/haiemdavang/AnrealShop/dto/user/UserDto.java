package com.haiemdavang.AnrealShop.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.haiemdavang.AnrealShop.dto.address.AddressDto;
import com.haiemdavang.AnrealShop.modal.enums.GenderType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {
    private String id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String avatarUrl;
    private GenderType gender;
    private LocalDate dob;
    private String role;
    private boolean verified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private AddressDto address;
    private int cartCount;
    private boolean hasShop;
    private boolean hasPassword;

}
