package com.haiemdavang.AnrealShop.controller.admin;

import com.haiemdavang.AnrealShop.dto.common.RejectRequest;
import com.haiemdavang.AnrealShop.dto.user.AccountType;
import com.haiemdavang.AnrealShop.dto.user.AdminUserListResponse;
import com.haiemdavang.AnrealShop.modal.enums.CancelBy;
import com.haiemdavang.AnrealShop.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminUserController {
    private final IUserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<AdminUserListResponse> getListOrderItems(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false, defaultValue = "") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate confirmSD,
            @RequestParam(required = false, defaultValue = "") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate confirmED,
            @RequestParam(required = false, defaultValue = "all") AccountType accountType,
            @RequestParam(required = false, defaultValue = "newest") String sortBy
    ) {

        LocalDateTime confirmSDTime = null;
        if (confirmSD!= null) {
            confirmSDTime = confirmSD.atTime(0,0,0);
        }
        LocalDateTime confirmEDTime = null;
        if (confirmED!= null) {
            confirmEDTime = confirmED.atTime(23,59,59);
        }
        return ResponseEntity.ok(userService.getListUser(page, limit, search, confirmSDTime, confirmEDTime, accountType, sortBy));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/{id}/disable")
    public ResponseEntity<?> disableUser(@PathVariable String id, @RequestBody RejectRequest request) {
        userService.softDelete(id, request.getReason(), CancelBy.ADMIN);
        return ResponseEntity.ok().build();
    }


}
