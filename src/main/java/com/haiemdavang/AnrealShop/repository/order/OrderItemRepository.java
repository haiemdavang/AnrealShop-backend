package com.haiemdavang.AnrealShop.repository.order;

import com.haiemdavang.AnrealShop.modal.entity.order.OrderItem;
import com.haiemdavang.AnrealShop.modal.enums.OrderTrackStatus;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.lang.Nullable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@NonNullApi
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> , JpaSpecificationExecutor<OrderItem> {
    @EntityGraph(value = "OrderItem.graph.forShop", type = EntityGraph.EntityGraphType.FETCH)
    List<OrderItem> findAll(@Nullable Specification<OrderItem> spec);


    @EntityGraph(attributePaths = {
            "productSku",
            "productSku.product",
            "productSku.attributes",
    })
    List<OrderItem> findByIdIn(Collection<String> ids);


    @EntityGraph(attributePaths = {
            "shopOrder",
            "shopOrder.order",
            "shopOrder.order.shippingAddress",
            "productSku",
            "productSku.product",
    })
    List<OrderItem> findByShopOrderIdInAndStatus(Collection<String> shopOrderIds, OrderTrackStatus status);

    @EntityGraph(attributePaths = {
            "order",
            "order.user",
            "productSku",
            "productSku.product",
    })
    Optional<OrderItem> findWithOrderAndProductById(String id);

}
