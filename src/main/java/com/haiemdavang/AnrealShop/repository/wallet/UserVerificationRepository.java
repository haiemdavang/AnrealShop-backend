package com.haiemdavang.AnrealShop.repository.wallet;

import com.haiemdavang.AnrealShop.modal.entity.wallet.UserVerification;
import com.haiemdavang.AnrealShop.modal.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerification, String> {

    Optional<UserVerification> findByUserId(String userId);

    Optional<UserVerification> findByUserIdAndStatus(String userId, VerificationStatus status);

    boolean existsByDocumentNumber(String documentNumber);

    boolean existsByUserIdAndStatusIn(String userId, java.util.Collection<VerificationStatus> statuses);

    List<UserVerification> findByUserIdIn(List<String> userIds);
}
