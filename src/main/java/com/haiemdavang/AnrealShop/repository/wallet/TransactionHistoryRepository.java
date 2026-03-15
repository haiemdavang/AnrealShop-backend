package com.haiemdavang.AnrealShop.repository.wallet;

import com.haiemdavang.AnrealShop.modal.entity.wallet.TransactionHistory;
import com.haiemdavang.AnrealShop.modal.enums.TransactionStatus;
import com.haiemdavang.AnrealShop.modal.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, String>,
        JpaSpecificationExecutor<TransactionHistory> {

    Page<TransactionHistory> findByWalletId(String walletId, Pageable pageable);

    Page<TransactionHistory> findByWalletIdAndTransactionType(String walletId, TransactionType transactionType, Pageable pageable);

    Page<TransactionHistory> findByWalletIdAndStatus(String walletId, TransactionStatus status, Pageable pageable);

    Page<TransactionHistory> findByWalletIdAndTransactionTypeAndStatus(
            String walletId, TransactionType transactionType, TransactionStatus status, Pageable pageable);
}
