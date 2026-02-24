package com.haiemdavang.AnrealShop.modal.entity.shop;


import com.haiemdavang.AnrealShop.modal.entity.address.ShopAddress;
import com.haiemdavang.AnrealShop.modal.entity.order.Order;
import com.haiemdavang.AnrealShop.modal.entity.order.OrderItem;
import com.haiemdavang.AnrealShop.modal.entity.shipping.Shipping;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.modal.enums.ShopOrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "order", "shop", "trackingHistory", "shippingAddress", "shipping", "orderItems"})
@EqualsAndHashCode(of = {"id", "user", "order", "shop"})
@NamedEntityGraph(
        name = "ShopOrder.graph.forShop",
        attributeNodes = {
                @NamedAttributeNode(value = "order", subgraph = "orderSubgraph"),
                @NamedAttributeNode("user"),
                @NamedAttributeNode("shop"),
                @NamedAttributeNode("shipping")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "orderSubgraph",
                        attributeNodes = @NamedAttributeNode("payment")
                )
        }
)
@Entity
@Table(name = "don_hang_cua_hang")
public class ShopOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_don_hang_cua_hang", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_don_hang", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dia_chi_cua_hang", nullable = false)
    private ShopAddress shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cua_hang", nullable = false)
    private Shop shop;

    @Column(name = "phi_giao_hang", nullable = false)
    @Builder.Default
    private Long shippingFee = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    @Builder.Default
    private ShopOrderStatus status = ShopOrderStatus.INIT_PROCESSING;


    @Column(name = "tong_tien", nullable = false)
    private Long totalAmount;

    @Column(name = "tong_khoi_luong", nullable = false)
    private Long totalWeight;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "shopOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ShopOrderTrack> trackingHistory = new HashSet<>();

    @OneToOne(mappedBy = "shopOrder", orphanRemoval = true)
    private Shipping shipping;

    public void addTrackingHistory(ShopOrderTrack track) {
        if (trackingHistory == null) trackingHistory = new HashSet<>();
        trackingHistory.add(track);
        track.setShopOrder(this);
    }

    @OneToMany(mappedBy = "shopOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<OrderItem> orderItems = new HashSet<>();

    public void addOrderItems(OrderItem orderItem) {
        if (orderItems == null) orderItems = new HashSet<>();
        orderItems.add(orderItem);
        orderItem.setShopOrder(this);
    }

    public void setStatus(ShopOrderStatus status) {
        this.status = status;
        ShopOrderTrack shopOrderTrack = new ShopOrderTrack(this, status, LocalDateTime.now());
        this.addTrackingHistory(shopOrderTrack);
    }

}
