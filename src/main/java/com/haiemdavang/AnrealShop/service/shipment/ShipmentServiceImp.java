package com.haiemdavang.AnrealShop.service.shipment;

import com.haiemdavang.AnrealShop.dto.address.AddressDto;
import com.haiemdavang.AnrealShop.dto.shipping.*;
import com.haiemdavang.AnrealShop.dto.shipping.search.CheckoutShippingFee;
import com.haiemdavang.AnrealShop.dto.shipping.search.PreparingStatus;
import com.haiemdavang.AnrealShop.dto.shipping.search.SearchTypeShipping;
import com.haiemdavang.AnrealShop.exception.AnrealShopException;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.exception.ForbiddenException;
import com.haiemdavang.AnrealShop.mapper.AddressMapper;
import com.haiemdavang.AnrealShop.mapper.ShipmentMapper;
import com.haiemdavang.AnrealShop.modal.entity.address.ShopAddress;
import com.haiemdavang.AnrealShop.modal.entity.address.UserAddress;
import com.haiemdavang.AnrealShop.modal.entity.cart.CartItem;
import com.haiemdavang.AnrealShop.modal.entity.order.OrderItem;
import com.haiemdavang.AnrealShop.modal.entity.product.ProductSku;
import com.haiemdavang.AnrealShop.modal.entity.shipping.Shipping;
import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import com.haiemdavang.AnrealShop.modal.entity.shop.ShopOrder;
import com.haiemdavang.AnrealShop.modal.enums.ShippingStatus;
import com.haiemdavang.AnrealShop.modal.enums.ShopOrderStatus;
import com.haiemdavang.AnrealShop.repository.order.ShopOrderRepository;
import com.haiemdavang.AnrealShop.repository.order.ShopOrderSpecification;
import com.haiemdavang.AnrealShop.repository.shipping.ShipSpecification;
import com.haiemdavang.AnrealShop.repository.shipping.ShipmentRepository;
import com.haiemdavang.AnrealShop.schedule.ShippingTemplateStringNote;
import com.haiemdavang.AnrealShop.security.SecurityUtils;
import com.haiemdavang.AnrealShop.service.IAddressService;
import com.haiemdavang.AnrealShop.service.ICartService;
import com.haiemdavang.AnrealShop.service.IProductService;
import com.haiemdavang.AnrealShop.service.IShipmentService;
import com.haiemdavang.AnrealShop.service.order.IOrderItemService;
import com.haiemdavang.AnrealShop.service.order.IShopOrderService;
import com.haiemdavang.AnrealShop.tech.kafka.dto.ShippingSyncMessage;
import com.haiemdavang.AnrealShop.utils.ApplicationInitHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ShipmentServiceImp implements IShipmentService {
    private final IGHNService ighnService;
    private final ICartService cartService;
    private final IAddressService addressService;
    private final IProductService productService;

    private final AddressMapper addressMapper;
    private final IOrderItemService orderItemService;
    private final ShipmentRepository shipmentRepository;
    private final SecurityUtils securityUtils;
    private final ShopOrderRepository shopOrderRepository;
    private final ShipmentMapper shipmentMapper;
    private final IShopOrderService shopOrderService;

    public ShipmentServiceImp(IGHNService ighnService, ICartService cartService, IAddressService addressService,
                              IProductService productService, AddressMapper addressMapper, IOrderItemService orderItemService,
                              ShipmentRepository shipmentRepository, SecurityUtils securityUtils,
                              ShopOrderRepository shopOrderRepository, ShipmentMapper shipmentMapper,
                              @Lazy IShopOrderService shopOrderService) {
        this.ighnService = ighnService;
        this.cartService = cartService;
        this.addressService = addressService;
        this.productService = productService;
        this.addressMapper = addressMapper;
        this.orderItemService = orderItemService;
        this.shipmentRepository = shipmentRepository;
        this.securityUtils = securityUtils;
        this.shopOrderRepository = shopOrderRepository;
        this.shipmentMapper = shipmentMapper;
        this.shopOrderService = shopOrderService;
    }

    @Override
    public List<CartShippingFee> getShippingFeeForCart(List<String> cartItemIds) {
        Map<Shop, Set<CartItem>> cartItemsByShop = cartService.getCartItemsByIdIn(cartItemIds);
        Set<String> ids = cartItemsByShop.keySet().stream().map(Shop::getId).collect(Collectors.toSet());
        Map<String, AddressDto> shopAddresses = addressService.getShopAddressByIdIn(ids);
        AddressDto userAddress = addressService.findAddressPrimary();

        List<CartShippingFee> result = new ArrayList<>();

        for (Shop s: cartItemsByShop.keySet()) {
            AddressDto shopAddress = shopAddresses.get(s.getId());
            int totalWeight = cartItemsByShop.get(s).stream()
                    .mapToInt(item -> item.getProductSku().getProduct().getWeight().intValue() * item.getQuantity())
                    .sum();
            InfoShipment info = InfoShipment.builder()
                    .from(shopAddress)
                    .to(userAddress)
                    .weight(totalWeight)
                    .build();
            InfoShippingOrder infoOrder = ighnService.getShippingOrderInfo(info);
            result.add(CartShippingFee.builder()
                    .shopId(s.getId())
                    .fee(infoOrder.getFee())
                    .leadTime(infoOrder.getLeadTime())
                    .isSuccess(infoOrder.isSuccess)
                    .serviceName(infoOrder.getServiceName())
                    .build());
        }

        return result;
    }

    @Override
    public List<CartShippingFee> getShippingFeeForCheckout(CheckoutShippingFee checkoutShippingFee) {
        UserAddress userAddress = addressService.getCurrentUserAddressById(checkoutShippingFee.getUserAddressId());
        List<ProductSku> productSkus = productService.getProductSkuByIdIn(checkoutShippingFee.getCheckoutItems().keySet());
        Map<ProductSku, Integer> productSkuIntegerMap = productSkus.stream()
                .collect(Collectors.toMap(
                        pk -> pk,
                        pk -> checkoutShippingFee.getCheckoutItems().get(pk.getId())
                ));

        Map<Shop, List<ProductSku>> shopListMap = productSkus.stream()
                .collect(Collectors.groupingBy(
                        sku -> sku.getProduct().getShop()
                ));

        Map<String, AddressDto> shopAddresses = addressService.getShopAddressByIdIn(shopListMap.keySet().stream().map(Shop::getId).collect(Collectors.toSet()));

        List<CartShippingFee> result = new ArrayList<>();
        for (Shop s: shopListMap.keySet()) {
            AddressDto shopAddress = shopAddresses.get(s.getId());
            int totalWeight = shopListMap.get(s).stream()
                    .mapToInt(item -> item.getProduct().getWeight().intValue() * productSkuIntegerMap.get(item))
                    .sum();
            InfoShipment info = InfoShipment.builder()
                    .from(shopAddress)
                    .to(addressMapper.toAddressDto(userAddress))
                    .weight(totalWeight)
                    .build();
            InfoShippingOrder infoOrder = ighnService.getShippingOrderInfo(info);
            result.add(CartShippingFee.builder()
                    .shopId(s.getId())
                    .fee(infoOrder.getFee())
                    .leadTime(infoOrder.getLeadTime())
                    .isSuccess(infoOrder.isSuccess)
                    .serviceName(infoOrder.getServiceName())
                    .build());
        }

        return result;
    }


    @Override
    public Map<ShopAddress, Long> getShippingFee(UserAddress userAddress, Map<ProductSku, Integer> productSkus) {
        Map<Shop, List<ProductSku>> productSkusByShop = productSkus.keySet().stream()
                .collect(Collectors.groupingBy(pk -> pk.getProduct().getShop(),
                        Collectors.mapping(pk -> pk, Collectors.toList())));

        Map<ShopAddress, Long> mapResult = new HashMap<>();
        for (Shop shop : productSkusByShop.keySet()) {
            ShopAddress shopAddress = addressService.getShopAddressByIdShop(shop.getId());
            int totalWeight = productSkusByShop.get(shop).stream()
                    .mapToInt(pk -> pk.getProduct().getWeight().intValue() * productSkus.get(pk))
                    .sum();
            InfoShipment info = InfoShipment.builder()
                    .from(addressMapper.toAddressDto(shopAddress))
                    .to(addressMapper.toAddressDto(userAddress))
                    .weight(totalWeight)
                    .build();
            int fetchFee = ighnService.getShippingOrderInfo(info).getFee();
            mapResult.put(shopAddress, (long) fetchFee);
        }
        return mapResult;

    }

    @Override
    @Transactional
    public void createShipments(CreateShipmentRequest createShipmentRequest) {
        List<OrderItem> orderItems = orderItemService.getForShipment(createShipmentRequest.getShopOrderIds());
//        ShopAddress fromAddress = addressService.getShopAddressByIdShop(createShipmentRequest.getAddressId());
        Shop currentShop = securityUtils.getCurrentUserShop();
        ShopAddress fromAddress = addressService.getShopAddressByIdShop(currentShop.getId());

        Map<ShopOrder, List<OrderItem>> orderListMap = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getShopOrder));

        for (ShopOrder shopOrder : orderListMap.keySet()) {
            Set<OrderItem> items = new HashSet<>(orderListMap.get(shopOrder));
            long totalWeight = items.stream()
                    .mapToInt(item -> item.getProductSku().getProduct().getWeight().intValue() * item.getQuantity())
                    .sum();
            InfoShipment info = InfoShipment.builder()
                    .from(addressMapper.toAddressDto(fromAddress))
                    .to(addressMapper.toAddressDto(shopOrder.getShippingAddress()))
                    .weight(totalWeight)
                    .build();
            InfoShippingOrder infoOrder = ighnService.getShippingOrderInfo(info);
            if (infoOrder.isSuccess) {
//                ighnService.createShippingOrder(shopOrder, items, infoOrder, createShipmentRequest.getNote());
                Shipping shipping = Shipping.builder()
                        .addressFrom(fromAddress)
                        .addressTo(shopOrder.getOrder().getShippingAddress())
                        .totalWeight(totalWeight)
                        .fee((long) infoOrder.getFee())
                        .note(createShipmentRequest.getNote())
                        .dayPickup(createShipmentRequest.getPickupDate())
                        .shopOrder(shopOrder)
                        .build();

                shipping.setStatus(ShippingStatus.ORDER_CREATED, ShippingTemplateStringNote.ORDER_CREATED_NOTES);
                shipmentRepository.save(shipping);
            } else {
                throw new AnrealShopException("SERVER_ERROR");
            }
        }
    }

    @Override
    @Transactional
    public void createShipments(String shopOrderId, BaseCreateShipmentRequest request) {
//        ShopAddress fromAddress = addressService.getShopAddressByIdShop(createShipmentRequest.getAddressId());
//        Shop currentShop = securityUtils.getCurrentUserShop();
//        ShopAddress fromAddress = addressService.getShopAddressByIdShop(currentShop.getId());
        ShopOrder shopOrder = shopOrderRepository.findById(shopOrderId)
                .orElseThrow(() -> new BadRequestException("SHOP_ORDER_NOT_FOUND"));

        Shipping shipping = Shipping.builder()
                .addressFrom(shopOrder.getShippingAddress())
                .addressTo(shopOrder.getOrder().getShippingAddress())
                .totalWeight(shopOrder.getTotalWeight())
                .fee(shopOrder.getShippingFee())
                .note(request.getNote())
                .dayPickup(request.getPickupDate())
                .shopOrder(shopOrder)
                .build();
        shipping.setStatus(ShippingStatus.ORDER_CREATED, ShippingTemplateStringNote.ORDER_CREATED_NOTES);
        shipmentRepository.save(shipping);
    }

    @Override
    @Transactional
    public String rejectById(String shippingId, String reason) {
        Shipping shipping = shipmentRepository.findById(shippingId)
                .orElseThrow(() -> new BadRequestException("SHIPPING_NOT_FOUND"));
        ShippingStatus  status = shipping.getStatus();
        if (status.equals(ShippingStatus.ORDER_CREATED) || status.equals(ShippingStatus.WAITING_FOR_PICKUP)) {
            shipping.setCancelReason(reason);
        } else {
            throw new ForbiddenException("FORBIDDEN_SHIPPING");
        }
        return shipping.getShopOrder().getId();
    }

    @Override
    @Transactional
    public Shipping processShippingSyncMessage(ShippingSyncMessage message) {
        Shipping shipping = shipmentRepository.findById(message.getId())
                .orElseThrow(() -> new BadRequestException("SHIPPING_NOT_FOUND"));
        shipping.setStatus(message.getStatus(), message.getNote());
        return shipmentRepository.save(shipping);
    }

    @Override
    public MyShopShippingListResponse getListForShop(int page, int limit, String search, SearchTypeShipping searchTypeShipping, PreparingStatus preparingStatus, String sortBy) {
        LocalDateTime now =  LocalDate.now().atTime(23, 59, 59);
        String shopId = securityUtils.getCurrentUserShop().getId();
        Specification<Shipping> shipSpecification = ShipSpecification.filter(shopId, now.minusMonths(2), now, search, searchTypeShipping, preparingStatus);
        Pageable pageable = PageRequest.of(page, limit, ApplicationInitHelper.getSortBy(sortBy));

        Page<Shipping> shippingList = shipmentRepository.findAll(shipSpecification, pageable);

        List<String> shippingIds = shippingList.get().map(Shipping::getId).toList();

        MyShopShippingListResponse response = MyShopShippingListResponse.builder()
                .currentPage(shippingList.getPageable().getPageNumber() + 1)
                .totalPages(shippingList.getTotalPages())
                .totalCount(shippingList.getTotalElements())
                .build();
        if (shippingIds.isEmpty()) {
            response.setOrderItemDtoSet(new HashSet<>());
        }else {
            Specification<ShopOrder> orderSpecification = ShopOrderSpecification.filter(shippingIds, search, searchTypeShipping);
            List<ShopOrder> shopOrders = shopOrderRepository.findAll(orderSpecification);
            if(shopOrders.size() != shippingIds.size())
                throw new BadRequestException("ORDER_NOT_FOUND");
            Set<ShippingItem> shippingItems = new HashSet<>();
            Map<Shipping, ShopOrder> shippingShopOrderMap = shopOrders.stream()
                    .filter(so -> so.getShipping() != null)
                    .collect(Collectors.toMap(ShopOrder::getShipping, so -> so));
            for (Shipping shipping : shippingList.getContent()) {
                ShopOrder exists = shippingShopOrderMap.get(shipping);
                ShippingItem shippingItem = shipmentMapper.toShippingItems(shipping, exists);
                shippingItems.add(shippingItem);
            }
            response.setOrderItemDtoSet(shippingItems);
        }
        return response;
    }

    @Override
    public Shipping getShippingByShopOrderId(String shopOrderId) {
        return shipmentRepository.findByShopOrderId(shopOrderId)
                .orElse(null);
    }

    @Override
    @Transactional
    public void updateShipmentStatus(List<String> shopOrderIds, ShippingStatus status, String note) {
        List<Shipping> shippings = shipmentRepository.findByShopOrderIdIn(shopOrderIds);
        for (Shipping shipping : shippings) {
            shipping.setStatus(status, note);
        }
        shipmentRepository.saveAll(shippings);
    }

    @Override
    public List<Shipping> getListShippingByShopOrderStatus(ShopOrderStatus shopOrderStatus) {
        return shipmentRepository.findAllByShopOrderStatus(shopOrderStatus);
    }

    @Override
    @Transactional
    public void processShippingStatusSync(String shippingId, ShippingStatus status, String note) {
        log.info("Processing shipping status sync directly: shippingId={}, status={}, note={}", shippingId, status, note);
        Shipping shipping = shipmentRepository.findById(shippingId)
                .orElseThrow(() -> new BadRequestException("SHIPPING_NOT_FOUND"));
        shipping.setStatus(status, note);
        shipmentRepository.save(shipping);
        if (status.equals(ShippingStatus.DELIVERED)) {
            shopOrderService.updateStatus(Collections.singletonList(shipping.getShopOrder().getId()), ShopOrderStatus.DELIVERED);
        }
    }

}
