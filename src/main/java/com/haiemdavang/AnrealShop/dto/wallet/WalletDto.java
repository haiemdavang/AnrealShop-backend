package com.haiemdavang.AnrealShop.dto.wallet;

import com.haiemdavang.AnrealShop.modal.enums.VerificationStatus;
import com.haiemdavang.AnrealShop.modal.enums.WalletOwnerType;
import com.haiemdavang.AnrealShop.modal.enums.WalletStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletDto {
    private String id;
    private String ownerId;
    private WalletOwnerType ownerType;
    private long availableBalance;
    private String currency;
    private WalletStatus status;
    private VerificationStatus verificationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
