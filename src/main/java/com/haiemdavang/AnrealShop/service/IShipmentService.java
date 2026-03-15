package com.haiemdavang.AnrealShop.service;

import com.haiemdavang.AnrealShop.dto.shipping.BaseCreateShipmentRequest;
import com.haiemdavang.AnrealShop.dto.shipping.CartShippingFee;
import com.haiemdavang.AnrealShop.dto.shipping.CreateShipmentRequest;
import com.haiemdavang.AnrealShop.dto.shipping.MyShopShippingListResponse;
import com.haiemdavang.AnrealShop.dto.shipping.search.CheckoutShippingFee;
import com.haiemdavang.AnrealShop.dto.shipping.search.PreparingStatus;
import com.haiemdavang.AnrealShop.dto.shipping.search.SearchTypeShipping;
import com.haiemdavang.AnrealShop.modal.entity.address.ShopAddress;
import com.haiemdavang.AnrealShop.modal.entity.address.UserAddress;
import com.haiemdavang.AnrealShop.modal.entity.product.ProductSku;
import com.haiemdavang.AnrealShop.modal.entity.shipping.Shipping;
import com.haiemdavang.AnrealShop.modal.enums.ShippingStatus;
import com.haiemdavang.AnrealShop.modal.enums.ShopOrderStatus;
import com.haiemdavang.AnrealShop.tech.kafka.dto.ShippingSyncMessage;

import java.util.List;
import java.util.Map;

public interface IShipmentService {
    List<CartShippingFee> getShippingFeeForCart(List<String> cartItemIds);

    Map<ShopAddress, Long> getShippingFee(UserAddress userAddress, Map<ProductSku, Integer> productSkus);

    void createShipments(CreateShipmentRequest createShipmentRequest);

    MyShopShippingListResponse getListForShop(int page, int limit, String search, SearchTypeShipping searchTypeShipping, PreparingStatus preparingStatus, String sortBy);

    Shipping getShippingByShopOrderId(String shopOrderId);

    void createShipments(String shopOrderId, BaseCreateShipmentRequest request);

    String rejectById(String shippingId, String reason);


    Shipping processShippingSyncMessage(ShippingSyncMessage message);

    void processShippingStatusSync(String shippingId, ShippingStatus status, String note);

    List<CartShippingFee> getShippingFeeForCheckout(CheckoutShippingFee checkoutShippingFee);

    void updateShipmentStatus(List<String> shopOrderIds, ShippingStatus status, String note);

    List<Shipping> getListShippingByShopOrderStatus(ShopOrderStatus shopOrderStatus);
}
