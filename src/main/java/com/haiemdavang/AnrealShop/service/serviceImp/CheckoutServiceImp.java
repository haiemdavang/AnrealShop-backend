package com.haiemdavang.AnrealShop.service.serviceImp;

import com.haiemdavang.AnrealShop.dto.address.AddressDto;
import com.haiemdavang.AnrealShop.dto.checkout.CheckoutInfoDto;
import com.haiemdavang.AnrealShop.dto.checkout.CheckoutRequestDto;
import com.haiemdavang.AnrealShop.dto.checkout.CheckoutResponseDto;
import com.haiemdavang.AnrealShop.dto.checkout.ItemProductCheckoutDto;
import com.haiemdavang.AnrealShop.dto.shipping.InfoShipment;
import com.haiemdavang.AnrealShop.dto.shipping.InfoShippingOrder;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.exception.ConflictException;
import com.haiemdavang.AnrealShop.mapper.CartMapper;
import com.haiemdavang.AnrealShop.mapper.ShopMapper;
import com.haiemdavang.AnrealShop.modal.entity.address.UserAddress;
import com.haiemdavang.AnrealShop.modal.entity.product.ProductSku;
import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import com.haiemdavang.AnrealShop.modal.enums.PaymentType;
import com.haiemdavang.AnrealShop.service.IAddressService;
import com.haiemdavang.AnrealShop.service.ICheckoutService;
import com.haiemdavang.AnrealShop.service.IProductService;
import com.haiemdavang.AnrealShop.service.order.IUserOrderService;
import com.haiemdavang.AnrealShop.service.shipment.IGHNService;
import com.haiemdavang.AnrealShop.tech.kafka.producer.NoticeKafkaProducer;
import com.haiemdavang.AnrealShop.dto.notice.NoticeTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImp implements ICheckoutService {
    private final IUserOrderService orderService;
    private final IAddressService addressService;
    private final IGHNService ighnService;
    private final IProductService productService;
    private final ShopMapper shopMapper;
    private final CartMapper cartMapper;

    private final NoticeKafkaProducer noticeKafkaProducer;


    @Override
    public CheckoutResponseDto checkout(CheckoutRequestDto requestDto, HttpServletRequest request) {
        UserAddress userAddress = addressService.getCurrentUserAddressById(requestDto.getAddressId());

        CheckoutResponseDto responseDto;
        if (requestDto.getPaymentMethod().equals(PaymentType.BANK_TRANSFER)){
            responseDto = orderService.createOrderBankTran(requestDto, userAddress, getClientIpAddress(request));
        } else if (requestDto.getPaymentMethod().equals(PaymentType.COD)) {
            responseDto = orderService.createOrderCOD(requestDto, userAddress);
        } else {
            throw new BadRequestException("PAYMENT_METHOD_NOT_SUPPORT");
        }

        if (responseDto != null) {
            noticeKafkaProducer.sendNoticeSyncMessage(NoticeTemplate.newOrderForShop(responseDto.getOrderId()));
        }
        return responseDto;
    }

    @Override
    public void validateItems(List<ItemProductCheckoutDto> items) {
        Map<String, Integer> mapItems = items.stream()
                .collect(Collectors.toMap(ItemProductCheckoutDto::getProductSkuId, ItemProductCheckoutDto::getQuantity));

        List<ProductSku> productSkus = productService.findByProductSkuIdIn(mapItems.keySet());
        boolean isValid = productSkus.stream()
                .allMatch(productSku -> {
                    Integer quantity = mapItems.get(productSku.getId());
                    return productSku.getQuantity() >= quantity;
                });
        if (!isValid)
            throw new ConflictException("ITEMS_NOT_ENOUGH_QUANTITY");
    }

    @Override
    public List<CheckoutInfoDto> getListCheckout(Map<String, Integer> idProductSkus) {

        List<ProductSku> productSkus = productService.findByProductSkuIdIn(idProductSkus.keySet());
        Map<Shop, Set<ProductSku>> shopSetMap = productSkus.stream().collect(
                Collectors.groupingBy(t -> t.getProduct().getShop(), Collectors.toSet())
        );

        Set<String> ids = shopSetMap.keySet().stream().map(Shop::getId).collect(Collectors.toSet());
        Map<String, AddressDto> shopAddresses = addressService.getShopAddressByIdIn(ids);
        AddressDto userAddress = addressService.findAddressPrimary();

        List<CheckoutInfoDto> result = new ArrayList<>();

        for (Shop s: shopSetMap.keySet()) {
            AddressDto shopAddress = shopAddresses.get(s.getId());
            int totalWeight = shopSetMap.get(s).stream()
                    .mapToInt(item -> item.getProduct().getWeight().intValue() * idProductSkus.get(item.getId())).sum();
            InfoShipment info = InfoShipment.builder()
                    .from(shopAddress)
                    .to(userAddress)
                    .weight(totalWeight)
                    .build();
            InfoShippingOrder infoOrder = ighnService.getShippingOrderInfo(info);
            result.add(CheckoutInfoDto.builder()
                    .shop(shopMapper.toBaseShopDto(s))
                    .items(shopSetMap.get(s).stream().map(i -> cartMapper.toCartItemDto(i, idProductSkus.get(i.getId()))).collect(Collectors.toList()))
                    .fee(infoOrder.getFee())
                    .serviceName(infoOrder.getServiceName())
                    .isSuccess(infoOrder.isSuccess)
                    .leadTime(infoOrder.getLeadTime()).build());
        }

        return result;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}