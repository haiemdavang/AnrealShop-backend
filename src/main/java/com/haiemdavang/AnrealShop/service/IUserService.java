package com.haiemdavang.AnrealShop.service;


import com.haiemdavang.AnrealShop.dto.user.*;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.modal.enums.CancelBy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public interface IUserService {

    void resetPassword(String email, String password);

    User findByEmail(String email);

    UserDto updateProfile(String email, @Valid ProfileRequest profileRequest);

    void deleteUser(String id);

    void registerUser(@Valid RegisterRequest request);

    UserDto findDtoByEmail(String username);

    UserDto verifyEmail(String email, String code);

    UserDto updatePassword(@Valid ChangePasswordDto changePasswordDto);

    AdminUserListResponse getListUser(int page, int limit, String search, LocalDateTime confirmSDTime, LocalDateTime confirmEDTime, AccountType accountType, String sortBy);

    void softDelete(String id, @NotBlank(message = "REASON_NOT_BLANK") String reason, CancelBy cancelBy);
}
