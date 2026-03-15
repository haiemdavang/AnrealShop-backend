package com.haiemdavang.AnrealShop.service.serviceImp;

import com.haiemdavang.AnrealShop.dto.wallet.AdminWalletDto;
import com.haiemdavang.AnrealShop.dto.wallet.AdminWalletListResponse;
import com.haiemdavang.AnrealShop.dto.wallet.TransactionHistoryDto;
import com.haiemdavang.AnrealShop.dto.wallet.TransactionHistoryListResponse;
import com.haiemdavang.AnrealShop.dto.wallet.UserVerificationDto;
import com.haiemdavang.AnrealShop.dto.wallet.VerifyWalletRequest;
import com.haiemdavang.AnrealShop.dto.wallet.WalletDto;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.exception.ConflictException;
import com.haiemdavang.AnrealShop.exception.ResourceNotFoundException;
import com.haiemdavang.AnrealShop.mapper.WalletMapper;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.modal.entity.wallet.TransactionHistory;
import com.haiemdavang.AnrealShop.modal.entity.wallet.UserVerification;
import com.haiemdavang.AnrealShop.modal.entity.wallet.Wallet;
import com.haiemdavang.AnrealShop.modal.enums.TransactionType;
import com.haiemdavang.AnrealShop.modal.enums.VerificationStatus;
import com.haiemdavang.AnrealShop.modal.enums.WalletOwnerType;
import com.haiemdavang.AnrealShop.modal.enums.WalletStatus;
import com.haiemdavang.AnrealShop.repository.user.UserRepository;
import com.haiemdavang.AnrealShop.repository.wallet.TransactionHistoryRepository;
import com.haiemdavang.AnrealShop.repository.wallet.UserVerificationRepository;
import com.haiemdavang.AnrealShop.repository.wallet.WalletRepository;
import com.haiemdavang.AnrealShop.repository.wallet.WalletSpecification;
import com.haiemdavang.AnrealShop.service.IWalletService;
import com.haiemdavang.AnrealShop.utils.ApplicationInitHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImp implements IWalletService {

    private final WalletRepository walletRepository;
    private final UserVerificationRepository userVerificationRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletMapper walletMapper;

    @Override
    @Transactional
    public WalletDto submitVerification(String userEmail, VerifyWalletRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadRequestException("USER_NOT_FOUND"));

        // Kiểm tra user đã có yêu cầu xác thực đang chờ duyệt hoặc đã xác thực chưa
        boolean hasActiveVerification = userVerificationRepository.existsByUserIdAndStatusIn(
                user.getId(),
                List.of(VerificationStatus.CHO_DUYET, VerificationStatus.DA_XAC_THUC)
        );
        if (hasActiveVerification) {
            throw new ConflictException("VERIFICATION_ALREADY_EXISTS");
        }

        // Kiểm tra số giấy tờ đã được sử dụng chưa
        if (userVerificationRepository.existsByDocumentNumber(request.getDocumentNumber())) {
            throw new ConflictException("DOCUMENT_NUMBER_ALREADY_USED");
        }

        // Tạo ví nếu chưa có
        Wallet wallet = walletRepository.findByOwnerIdAndOwnerType(user.getId(), WalletOwnerType.NGUOI_DUNG)
                .orElseGet(() -> {
                    Wallet newWallet = Wallet.builder()
                            .ownerId(user.getId())
                            .ownerType(WalletOwnerType.NGUOI_DUNG)
                            .availableBalance(0)
                            .currency("VND")
                            .paymentPassword(passwordEncoder.encode(request.getPaymentPassword()))
                            .status(WalletStatus.DANG_HOAT_DONG)
                            .build();
                    return walletRepository.save(newWallet);
                });

        // Nếu ví đã tồn tại nhưng chưa có mật khẩu thanh toán, cập nhật mật khẩu
        if (wallet.getPaymentPassword() == null) {
            wallet.setPaymentPassword(passwordEncoder.encode(request.getPaymentPassword()));
            walletRepository.save(wallet);
        }

        // Tạo bản ghi xác thực
        UserVerification verification = UserVerification.builder()
                .user(user)
                .realFullName(request.getRealFullName())
                .documentNumber(request.getDocumentNumber())
                .documentType(request.getDocumentType())
                .dateOfBirth(request.getDateOfBirth())
                .frontImageUrl(request.getFrontImageUrl())
                .backImageUrl(request.getBackImageUrl())
                .portraitImageUrl(request.getPortraitImageUrl())
                .status(VerificationStatus.CHO_DUYET)
                .build();
        userVerificationRepository.save(verification);

        return walletMapper.toWalletDto(wallet, verification.getStatus());
    }

    @Override
    public WalletDto getMyWallet(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadRequestException("USER_NOT_FOUND"));

        Wallet wallet = walletRepository.findByOwnerIdAndOwnerType(user.getId(), WalletOwnerType.NGUOI_DUNG)
                .orElseThrow(() -> new ResourceNotFoundException("WALLET_NOT_FOUND"));

        VerificationStatus verificationStatus = userVerificationRepository.findByUserId(user.getId())
                .map(UserVerification::getStatus)
                .orElse(null);

        return walletMapper.toWalletDto(wallet, verificationStatus);
    }

    @Override
    public UserVerificationDto getMyVerification(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadRequestException("USER_NOT_FOUND"));

        UserVerification verification = userVerificationRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("VERIFICATION_NOT_FOUND"));

        return walletMapper.toUserVerificationDto(verification);
    }

    @Override
    @Transactional
    public AdminWalletDto approveVerification(String walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("WALLET_NOT_FOUND"));

        UserVerification verification = userVerificationRepository.findByUserId(wallet.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("VERIFICATION_NOT_FOUND"));

        if (verification.getStatus() != VerificationStatus.CHO_DUYET) {
            throw new BadRequestException("VERIFICATION_NOT_PENDING");
        }

        // Cập nhật trạng thái xác thực
        verification.setStatus(VerificationStatus.DA_XAC_THUC);
        verification.setApprovedAt(LocalDateTime.now());
        verification.setRejectionReason(null);
        userVerificationRepository.save(verification);

        // Cập nhật trạng thái ví
        wallet.setStatus(WalletStatus.DANG_HOAT_DONG);
        walletRepository.save(wallet);

        // Cập nhật trạng thái xác thực của user
        User user = verification.getUser();
        user.setVerify(true);
        userRepository.save(user);

        log.info("Approved verification for wallet {} user {}", walletId, user.getId());
        return walletMapper.toAdminWalletDto(wallet, user, verification.getStatus());
    }

    @Override
    @Transactional
    public AdminWalletDto rejectVerification(String walletId, String reason) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("WALLET_NOT_FOUND"));

        UserVerification verification = userVerificationRepository.findByUserId(wallet.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("VERIFICATION_NOT_FOUND"));

        if (verification.getStatus() != VerificationStatus.CHO_DUYET) {
            throw new BadRequestException("VERIFICATION_NOT_PENDING");
        }

        // Cập nhật trạng thái xác thực
        verification.setStatus(VerificationStatus.BI_TU_CHOI);
        verification.setRejectionReason(reason);
        userVerificationRepository.save(verification);

        // Cập nhật trạng thái ví -> tạm khóa
        wallet.setStatus(WalletStatus.TAM_KHOA);
        walletRepository.save(wallet);

        User user = verification.getUser();
        log.info("Rejected verification for wallet {} user {}", walletId, user.getId());
        return walletMapper.toAdminWalletDto(wallet, user, verification.getStatus());
    }

    @Override
    public boolean verifyPaymentPassword(String userEmail, String paymentPassword) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadRequestException("USER_NOT_FOUND"));

        Wallet wallet = walletRepository.findByOwnerIdAndOwnerType(user.getId(), WalletOwnerType.NGUOI_DUNG)
                .orElseThrow(() -> new ResourceNotFoundException("WALLET_NOT_FOUND"));

        if (wallet.getPaymentPassword() == null) {
            throw new BadRequestException("PAYMENT_PASSWORD_NOT_SET");
        }

        return passwordEncoder.matches(paymentPassword, wallet.getPaymentPassword());
    }

    @Override
    public AdminWalletListResponse getListWallets(int page, int limit, String searchUser, WalletStatus walletStatus, String sortBy) {
        Specification<Wallet> spec = WalletSpecification.filter(searchUser, walletStatus);
        Pageable pageable = PageRequest.of(page, limit, ApplicationInitHelper.getSortBy(sortBy));

        Page<Wallet> walletPage = walletRepository.findAll(spec, pageable);
        List<Wallet> wallets = walletPage.getContent();

        // Lấy danh sách ownerId để query user và verification
        List<String> ownerIds = wallets.stream().map(Wallet::getOwnerId).toList();

        // Batch query users
        Map<String, User> userMap = userRepository.findAllById(ownerIds).stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        // Batch query verifications
        Map<String, UserVerification> verificationMap = userVerificationRepository.findByUserIdIn(ownerIds).stream()
                .collect(Collectors.toMap(v -> v.getUser().getId(), v -> v, (v1, v2) -> v1));

        List<AdminWalletDto> walletDtos = wallets.stream().map(wallet -> {
            User user = userMap.get(wallet.getOwnerId());
            UserVerification verification = verificationMap.get(wallet.getOwnerId());
            VerificationStatus verificationStatus = verification != null ? verification.getStatus() : null;
            return walletMapper.toAdminWalletDto(wallet, user, verificationStatus);
        }).toList();

        return AdminWalletListResponse.builder()
                .wallets(walletDtos)
                .totalPages(walletPage.getTotalPages())
                .currentPage(page)
                .totalCount(walletPage.getTotalElements())
                .build();
    }

    @Override
    public UserVerificationDto getWalletVerificationDetail(String walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("WALLET_NOT_FOUND"));

        UserVerification verification = userVerificationRepository.findByUserId(wallet.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("VERIFICATION_NOT_FOUND"));

        return walletMapper.toUserVerificationDto(verification);
    }

    @Override
    public TransactionHistoryListResponse getTransactionHistory(
            String userEmail, int page, int limit, TransactionType transactionType, String sortBy) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadRequestException("USER_NOT_FOUND"));

        Wallet wallet = walletRepository.findByOwnerIdAndOwnerType(user.getId(), WalletOwnerType.NGUOI_DUNG)
                .orElseThrow(() -> new ResourceNotFoundException("WALLET_NOT_FOUND"));

        Pageable pageable = PageRequest.of(page, limit, ApplicationInitHelper.getSortBy(sortBy));

        Page<TransactionHistory> transactionPage;
        if (transactionType != null) {
            transactionPage = transactionHistoryRepository.findByWalletIdAndTransactionType(
                    wallet.getId(), transactionType, pageable);
        } else {
            transactionPage = transactionHistoryRepository.findByWalletId(wallet.getId(), pageable);
        }

        List<TransactionHistoryDto> transactionDtos = transactionPage.getContent().stream()
                .map(walletMapper::toTransactionHistoryDto)
                .toList();

        return TransactionHistoryListResponse.builder()
                .transactions(transactionDtos)
                .totalPages(transactionPage.getTotalPages())
                .currentPage(page)
                .totalCount(transactionPage.getTotalElements())
                .build();
    }
}
