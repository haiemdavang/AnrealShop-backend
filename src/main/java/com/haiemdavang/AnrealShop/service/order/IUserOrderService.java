package com.haiemdavang.AnrealShop.service.order;

import com.haiemdavang.AnrealShop.dto.checkout.CheckoutRequestDto;
import com.haiemdavang.AnrealShop.dto.checkout.CheckoutResponseDto;
import com.haiemdavang.AnrealShop.dto.order.UserOrderListResponse;
import com.haiemdavang.AnrealShop.dto.order.search.SearchType;
import com.haiemdavang.AnrealShop.dto.payment.PaymentResponseDto;
import com.haiemdavang.AnrealShop.modal.entity.address.UserAddress;

public interface IUserOrderService {

    CheckoutResponseDto createOrderBankTran(CheckoutRequestDto requestDto, UserAddress userAddress);
    CheckoutResponseDto createOrderCOD(CheckoutRequestDto requestDto, UserAddress userAddress);

    void handleSuccessfulPayment(String orderId);
    void handleFailedPayment(String orderId, String responseCode);
    PaymentResponseDto getPaymentResult(String orderId);

    UserOrderListResponse getListOrderItems(int page, int limit, String status, String search, SearchType searchType, String sortBy);

}
