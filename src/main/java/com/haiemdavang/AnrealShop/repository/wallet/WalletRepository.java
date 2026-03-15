package com.haiemdavang.AnrealShop.repository.wallet;

import com.haiemdavang.AnrealShop.modal.entity.wallet.Wallet;
import com.haiemdavang.AnrealShop.modal.enums.WalletOwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String>, JpaSpecificationExecutor<Wallet> {

    Optional<Wallet> findByOwnerIdAndOwnerType(String ownerId, WalletOwnerType ownerType);

    boolean existsByOwnerIdAndOwnerType(String ownerId, WalletOwnerType ownerType);
}
