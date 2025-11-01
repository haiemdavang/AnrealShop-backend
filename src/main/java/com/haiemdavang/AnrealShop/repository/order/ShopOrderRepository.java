package com.haiemdavang.AnrealShop.repository.order;

import com.haiemdavang.AnrealShop.modal.entity.shop.ShopOrder;
import com.haiemdavang.AnrealShop.modal.enums.ShopOrderStatus;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@NonNullApi
@Repository
public interface ShopOrderRepository extends JpaRepository<ShopOrder, String>, JpaSpecificationExecutor<ShopOrder> {

    Page<ShopOrder> findAll(@Nullable Specification<ShopOrder> orderSpecification, Pageable pageable);

    List<ShopOrder> findAll(@Nullable Specification<ShopOrder> orderSpecification);


    @Query(value = "SELECT so FROM ShopOrder so " +
            "LEFT JOIN FETCH so.shop s " +
            "LEFT JOIN FETCH so.orderItems oi " +
            "LEFT JOIN FETCH oi.productSku sku " +
            "LEFT JOIN FETCH sku.product p " +
            "LEFT JOIN FETCH so.order o " +
            "LEFT JOIN FETCH o.shippingAddress a " +
            "LEFT JOIN FETCH a.province " +
            "LEFT JOIN FETCH a.district " +
            "LEFT JOIN FETCH a.ward " +
            "LEFT JOIN FETCH o.payment pay " +
            "LEFT JOIN FETCH so.trackingHistory " +
            "LEFT JOIN FETCH so.shipping " +
            "WHERE so.id = :shopOrderId")
    ShopOrder findWithFullInfoById(String shopOrderId);

    @Query(value = "SELECT so FROM ShopOrder so " +
            "LEFT JOIN FETCH so.orderItems oi " +
            "LEFT JOIN FETCH oi.productSku ps " +
            "LEFT JOIN FETCH so.order o " +
            "LEFT JOIN FETCH so.trackingHistory " +
            "WHERE so.id = :shopOrderId")
    ShopOrder findWithOrderItemById(String shopOrderId);

//    cho scheduler
    List<ShopOrder> findAllByStatus(ShopOrderStatus status);

    @Override
    @EntityGraph(attributePaths = {
            "order",
            "order.shippingAddress",
    })
    Optional<ShopOrder> findById(String s);

    @EntityGraph(attributePaths = {
            "orderItems",
            "orderItems.productSku",
            "orderItems.trackingHistory",
            "shop",
            "shop.user",
            "trackingHistory"
    })
    Set<ShopOrder> findByIdIn(Collection<String> ids);

    @Query(value = "SELECT so FROM ShopOrder so " +
            "LEFT JOIN FETCH so.order o " +
            "LEFT JOIN FETCH so.orderItems oi " +
            "LEFT JOIN FETCH oi.trackingHistory th " +
            "LEFT JOIN FETCH oi.productSku sku " +
            "LEFT JOIN FETCH so.trackingHistory sht " +
            "WHERE so.id = :shopOrderId")
    ShopOrder findWithFullOrderItemById(String shopOrderId);

    @EntityGraph(attributePaths = {
            "shop",
            "shop.user",
    })
    List<ShopOrder> findByOrderId(String orderId);
}
