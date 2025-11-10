package com.haiemdavang.AnrealShop.service.serviceImp;

import com.haiemdavang.AnrealShop.dto.user.*;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.exception.ConflictException;
import com.haiemdavang.AnrealShop.mapper.UserMapper;
import com.haiemdavang.AnrealShop.modal.entity.user.Role;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.modal.enums.CancelBy;
import com.haiemdavang.AnrealShop.modal.enums.RoleName;
import com.haiemdavang.AnrealShop.repository.user.UserRepository;
import com.haiemdavang.AnrealShop.repository.user.UserSpecification;
import com.haiemdavang.AnrealShop.security.SecurityUtils;
import com.haiemdavang.AnrealShop.service.IAddressService;
import com.haiemdavang.AnrealShop.service.ICartService;
import com.haiemdavang.AnrealShop.service.IShopService;
import com.haiemdavang.AnrealShop.service.IUserService;
import com.haiemdavang.AnrealShop.tech.mail.service.IMailService;
import com.haiemdavang.AnrealShop.utils.ApplicationInitHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements IUserService {
    private final UserRepository userRepository;
    private final RoleServiceImp roleService;
    private final IAddressService addressServiceImp;
    private final ICartService cartServiceImp;
    private final IShopService shopServiceImp;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final IMailService mailService;
    private final SecurityUtils securityUtils;


    @Override
    @Transactional
    public void resetPassword(String email, String password) {
        User user = findByEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setVerify(true);
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("USER_NOT_FOUND"));
    }

    @Override
    @Transactional
    public UserDto updateProfile(String email, ProfileRequest profileRequest) {
        User user = findByEmail(email);
        user = userMapper.updateUserFromProfileRequest(user, profileRequest);
        userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public void deleteUser(String id) {
        if(userRepository.existsById(id)) {
            userRepository.deleteById(id);
        }
    }

    @Override
    public void registerUser(RegisterRequest request) {
        if(userRepository.existsByEmail(request.email())) {
            throw new ConflictException("EMAIL_ALREADY_EXISTS");
        }
        User user = userMapper.createUserFromRegisterRequest(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleService.getRoleByName(RoleName.USER);
        user.setAvatarUrl(ApplicationInitHelper.IMAGE_USER_DEFAULT);
        user.setRole(role);
        userRepository.save(user);
    }

    @Override
    public UserDto findDtoByEmail(String username) {
        UserDto userDto =  userMapper.toUserDto(findByEmail(username));
        userDto.setHasShop(shopServiceImp.isExistByUserId(userDto.getId()));
        userDto.setAddress(addressServiceImp.findAddressPrimaryOrNull());
        userDto.setCartCount(cartServiceImp.countByUserId(userDto.getId()));
        return userDto;
    }

    @Override
    @Transactional
    public UserDto verifyEmail(String email, String code) {
        if (code == null || code.isEmpty() || !mailService.verifyOTP(code, email)) {
            throw new BadRequestException("INVALID_VERIFICATION_CODE");
        }
        User user = securityUtils.getCurrentUser();
        user.setVerify(true);
        userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto updatePassword(ChangePasswordDto changePasswordDto) {
        User user = securityUtils.getCurrentUser();
        if (user.getPassword() != null && !user.getPassword().isEmpty() && !passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("OLD_PASSWORD_INCORRECT");
        }
        user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
        userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public AdminUserListResponse getListUser(int page, int limit, String search, LocalDateTime confirmSDTime, LocalDateTime confirmEDTime, AccountType accountType, String sortBy) {
        Specification<User> userSpecification = UserSpecification.filter(search, confirmSDTime, confirmEDTime, accountType);
        Pageable pageable = PageRequest.of(page, limit, ApplicationInitHelper.getSortBy(sortBy));

        Page<User> users = userRepository.findAll(userSpecification, pageable);
        List<UserManagerDto> userManagerListDto = userMapper.toUserManagerDtoList(users.getContent());

        return AdminUserListResponse.builder()
                .users(userManagerListDto)
                .totalPages(users.getTotalPages())
                .currentPage(page)
                .totalCount(users.getTotalElements())
                .build();
    }

    @Override
    @Transactional
    public void softDelete(String id, String reason, CancelBy cancelBy) {
        User user = findByEmail(id);
        user.setDeleted(true);
        user.setDeleteReason(reason);

        userRepository.save(user);
    }

}
