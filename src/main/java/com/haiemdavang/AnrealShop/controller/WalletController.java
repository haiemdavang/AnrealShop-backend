package com.haiemdavang.AnrealShop.controller;

import com.haiemdavang.AnrealShop.dto.common.RejectRequest;
import com.haiemdavang.AnrealShop.dto.wallet.AdminWalletDto;
import com.haiemdavang.AnrealShop.dto.wallet.AdminWalletListResponse;
import com.haiemdavang.AnrealShop.dto.wallet.TransactionHistoryListResponse;
import com.haiemdavang.AnrealShop.dto.wallet.UserVerificationDto;
import com.haiemdavang.AnrealShop.dto.wallet.VerifyPasswordRequest;
import com.haiemdavang.AnrealShop.dto.wallet.VerifyWalletRequest;
import com.haiemdavang.AnrealShop.dto.wallet.WalletDto;
import com.haiemdavang.AnrealShop.modal.enums.TransactionType;
import com.haiemdavang.AnrealShop.modal.enums.WalletStatus;
import com.haiemdavang.AnrealShop.security.userDetails.UserDetailSecu;
import com.haiemdavang.AnrealShop.service.IWalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallet")
public class WalletController {

    private final IWalletService walletService;

    @PostMapping("/verify")
    public ResponseEntity<WalletDto> submitVerification(
            @AuthenticationPrincipal UserDetailSecu userDetails,
            @Valid @RequestBody VerifyWalletRequest request) {
        WalletDto walletDto = walletService.submitVerification(userDetails.getUsername(), request);
        return ResponseEntity.ok(walletDto);
    }

    @GetMapping("/me")
    public ResponseEntity<WalletDto> getMyWallet(
            @AuthenticationPrincipal UserDetailSecu userDetails) {
        return ResponseEntity.ok(walletService.getMyWallet(userDetails.getUsername()));
    }

    @GetMapping("/verification")
    public ResponseEntity<UserVerificationDto> getMyVerification(
            @AuthenticationPrincipal UserDetailSecu userDetails) {
        return ResponseEntity.ok(walletService.getMyVerification(userDetails.getUsername()));
    }


    @PostMapping("/verify-password")
    public ResponseEntity<Map<String, Object>> verifyPaymentPassword(
            @AuthenticationPrincipal UserDetailSecu userDetails,
            @Valid @RequestBody VerifyPasswordRequest request) {
        boolean matched = walletService.verifyPaymentPassword(
                userDetails.getUsername(), request.getPaymentPassword());
        return ResponseEntity.ok(Map.of(
                "verified", matched,
                "message", matched ? "Mật khẩu thanh toán chính xác" : "Mật khẩu thanh toán không đúng"
        ));
    }

    @GetMapping("/transactions")
    public ResponseEntity<TransactionHistoryListResponse> getTransactionHistory(
            @AuthenticationPrincipal UserDetailSecu userDetails,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false, defaultValue = "newest") String sortBy) {
        return ResponseEntity.ok(walletService.getTransactionHistory(
                userDetails.getUsername(), page, limit, transactionType, sortBy));
    }

    @PutMapping("/admin/wallets/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminWalletDto> approveVerification(@PathVariable String id) {
        return ResponseEntity.ok(walletService.approveVerification(id));
    }

    @GetMapping("/admin/wallets")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminWalletListResponse> getListWallets(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false) String searchUser,
            @RequestParam(required = false) WalletStatus walletStatus,
            @RequestParam(required = false, defaultValue = "newest") String sortBy) {
        return ResponseEntity.ok(walletService.getListWallets(page, limit, searchUser, walletStatus, sortBy));
    }

    @GetMapping("/admin/wallets/{id}/verification")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserVerificationDto> getWalletVerificationDetail(@PathVariable String id) {
        return ResponseEntity.ok(walletService.getWalletVerificationDetail(id));
    }

    @PutMapping("/admin/wallets/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminWalletDto> rejectVerification(
            @PathVariable String id,
            @Valid @RequestBody RejectRequest rejectRequest) {
        return ResponseEntity.ok(walletService.rejectVerification(id, rejectRequest.getReason()));
    }
}
