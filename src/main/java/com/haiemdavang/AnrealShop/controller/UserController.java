package com.haiemdavang.AnrealShop.controller;

import com.haiemdavang.AnrealShop.dto.user.ProfileRequest;
import com.haiemdavang.AnrealShop.dto.user.RegisterRequest;
import com.haiemdavang.AnrealShop.dto.user.ChangePasswordDto;
import com.haiemdavang.AnrealShop.dto.user.UserDto;
import com.haiemdavang.AnrealShop.security.userDetails.UserDetailSecu;
import com.haiemdavang.AnrealShop.service.serviceInter.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final IUserService userService;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        userService.registerUser(request);
        return ResponseEntity.ok(Map.of("message", "Tạo tài khoản thành công"));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal UserDetailSecu userDetails) {
        return ResponseEntity.ok(userService.findDtoByEmail(userDetails.getUsername()));
    }


//
//    @GetMapping("/{id}")
//    public ResponseEntity<ResponseDto<UserDto>> getUserById(@PathVariable String id) {
//        User user = userService.findById(id);
//        UserDto userDto = userMapper.toUserDto(user);
//        ResponseDto<UserDto> response = ResponseDto.success(
//                userDto,
//                "Lấy thông tin người dùng thành công"
//        );
//        return ResponseEntity.ok(response);
//    }
    
    @PutMapping("/update-profile")
    public ResponseEntity<UserDto> updateProfile(@Valid @RequestBody ProfileRequest profileRequest, @AuthenticationPrincipal UserDetailSecu userDetails) {
        return ResponseEntity.ok(userService.updateProfile(userDetails.getEmail(), profileRequest));
    }

    @PutMapping("/verify-email")
    public ResponseEntity<UserDto> verifyEmail(@RequestParam String code, @AuthenticationPrincipal UserDetailSecu userDetails) {
        return ResponseEntity.ok(userService.verifyEmail(userDetails.getEmail(), code));
    }

    @PostMapping("/change-password")
    public ResponseEntity<UserDto> updatePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        return ResponseEntity.ok(userService.updatePassword(changePasswordDto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Xóa người dùng thành công"));
    }
}
