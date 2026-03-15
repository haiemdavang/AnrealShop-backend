package com.haiemdavang.AnrealShop.service;

import com.haiemdavang.AnrealShop.dto.wallet.AdminWalletDto;
import com.haiemdavang.AnrealShop.dto.wallet.AdminWalletListResponse;
import com.haiemdavang.AnrealShop.dto.wallet.TransactionHistoryListResponse;
import com.haiemdavang.AnrealShop.dto.wallet.UserVerificationDto;
import com.haiemdavang.AnrealShop.dto.wallet.VerifyWalletRequest;
import com.haiemdavang.AnrealShop.dto.wallet.WalletDto;
import com.haiemdavang.AnrealShop.modal.enums.TransactionType;
import com.haiemdavang.AnrealShop.modal.enums.WalletStatus;

public interface IWalletService {

    /**
     * Gửi yêu cầu xác thực ví (KYC) - tạo ví + bản ghi xác thực
     */
    WalletDto submitVerification(String userEmail, VerifyWalletRequest request);

    /**
     * Lấy thông tin ví của user hiện tại
     */
    WalletDto getMyWallet(String userEmail);

    /**
     * Lấy thông tin xác thực của user hiện tại
     */
    UserVerificationDto getMyVerification(String userEmail);

    /**
     * Admin duyệt xác thực - cập nhật trạng thái UserVerification + Wallet, trả về WalletDto
     */
    AdminWalletDto approveVerification(String walletId);

    /**
     * Admin từ chối xác thực - cập nhật trạng thái UserVerification + Wallet, trả về WalletDto
     */
    AdminWalletDto rejectVerification(String walletId, String reason);

    /**
     * Xác thực mật khẩu thanh toán của ví
     */
    boolean verifyPaymentPassword(String userEmail, String paymentPassword);

    /**
     * Admin lấy danh sách ví đã đăng ký với các điều kiện lọc
     */
    AdminWalletListResponse getListWallets(int page, int limit, String searchUser, WalletStatus walletStatus, String sortBy);

    /**
     * Admin xem chi tiết xác thực (UserVerification) theo walletId
     */
    UserVerificationDto getWalletVerificationDetail(String walletId);

    /**
     * Lấy lịch sử giao dịch của ví người dùng hiện tại
     */
    TransactionHistoryListResponse getTransactionHistory(
            String userEmail, int page, int limit, TransactionType transactionType, String sortBy);
}
