package com.haiemdavang.AnrealShop.mapper;

import com.haiemdavang.AnrealShop.dto.wallet.AdminWalletDto;
import com.haiemdavang.AnrealShop.dto.wallet.TransactionHistoryDto;
import com.haiemdavang.AnrealShop.dto.wallet.UserVerificationDto;
import com.haiemdavang.AnrealShop.dto.wallet.WalletDto;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.modal.entity.wallet.TransactionHistory;
import com.haiemdavang.AnrealShop.modal.entity.wallet.UserVerification;
import com.haiemdavang.AnrealShop.modal.entity.wallet.Wallet;
import com.haiemdavang.AnrealShop.modal.enums.VerificationStatus;
import org.springframework.stereotype.Service;


@Service
public class WalletMapper {

    public WalletDto toWalletDto(Wallet wallet, VerificationStatus verificationStatus) {
        if (wallet == null) return null;
        return WalletDto.builder()
                .id(wallet.getId())
                .ownerId(wallet.getOwnerId())
                .ownerType(wallet.getOwnerType())
                .availableBalance(wallet.getAvailableBalance())
                .currency(wallet.getCurrency())
                .status(wallet.getStatus())
                .verificationStatus(verificationStatus)
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }

    public AdminWalletDto toAdminWalletDto(Wallet wallet, User user, VerificationStatus verificationStatus) {
        if (wallet == null) return null;
        return AdminWalletDto.builder()
                .id(wallet.getId())
                .ownerId(wallet.getOwnerId())
                .ownerType(wallet.getOwnerType())
                .availableBalance(wallet.getAvailableBalance())
                .currency(wallet.getCurrency())
                .status(wallet.getStatus())
                .verificationStatus(verificationStatus)
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .userEmail(user != null ? user.getEmail() : null)
                .userFullName(user != null ? user.getFullName() : null)
                .userAvatarUrl(user != null ? user.getAvatarUrl() : null)
                .userPhoneNumber(user != null ? user.getPhoneNumber() : null)
                .build();
    }

    public UserVerificationDto toUserVerificationDto(UserVerification verification) {
        if (verification == null) return null;
        return UserVerificationDto.builder()
                .id(verification.getId())
                .realFullName(verification.getRealFullName())
                .documentNumber(verification.getDocumentNumber())
                .documentType(verification.getDocumentType())
                .dateOfBirth(verification.getDateOfBirth())
                .frontImageUrl(verification.getFrontImageUrl())
                .backImageUrl(verification.getBackImageUrl())
                .portraitImageUrl(verification.getPortraitImageUrl())
                .status(verification.getStatus())
                .rejectionReason(verification.getRejectionReason())
                .approvedAt(verification.getApprovedAt())
                .createdAt(verification.getCreatedAt())
                .build();
    }

    public TransactionHistoryDto toTransactionHistoryDto(TransactionHistory transaction) {
        if (transaction == null) return null;
        return TransactionHistoryDto.builder()
                .id(transaction.getId())
                .walletId(transaction.getWallet().getId())
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .balanceBefore(transaction.getBalanceBefore())
                .balanceAfter(transaction.getBalanceAfter())
                .status(transaction.getStatus())
                .referenceCode(transaction.getReferenceCode())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
