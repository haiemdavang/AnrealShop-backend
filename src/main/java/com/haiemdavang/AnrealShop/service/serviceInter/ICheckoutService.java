package com.haiemdavang.AnrealShop.service.serviceInter;

import com.haiemdavang.AnrealShop.dto.checkout.ItemProductCheckoutDto;
import com.haiemdavang.AnrealShop.dto.checkout.CheckoutInfoDto;
import com.haiemdavang.AnrealShop.dto.checkout.CheckoutRequestDto;
import com.haiemdavang.AnrealShop.dto.checkout.CheckoutResponseDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface ICheckoutService {

    CheckoutResponseDto checkout(@Valid CheckoutRequestDto requestDto);

    void validateItems(List<ItemProductCheckoutDto> items);

    List<CheckoutInfoDto> getListCheckout(Map<String, Integer> idProductSkus);

    CheckoutResponseDto DecreaseBeforeCheckout(@Valid CheckoutRequestDto requestDto);

}
