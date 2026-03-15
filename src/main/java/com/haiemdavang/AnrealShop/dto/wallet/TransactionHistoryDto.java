package com.haiemdavang.AnrealShop.dto.wallet;

import com.haiemdavang.AnrealShop.modal.enums.TransactionStatus;
import com.haiemdavang.AnrealShop.modal.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryDto {
    private String id;
    private String walletId;
    private TransactionType transactionType;
    private long amount;
    private long balanceBefore;
    private long balanceAfter;
    private TransactionStatus status;
    private String referenceCode;
    private String description;
    private LocalDateTime createdAt;
}