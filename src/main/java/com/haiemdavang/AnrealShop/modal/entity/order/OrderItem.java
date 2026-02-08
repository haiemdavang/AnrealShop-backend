package com.haiemdavang.AnrealShop.modal.entity.order;


import com.haiemdavang.AnrealShop.modal.entity.product.ProductSku;
import com.haiemdavang.AnrealShop.modal.entity.shop.ShopOrder;
import com.haiemdavang.AnrealShop.modal.enums.CancelBy;
import com.haiemdavang.AnrealShop.modal.enums.OrderTrackStatus;
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
@ToString(exclude = { "productSku", "order", "shopOrder", "trackingHistory"})
@EqualsAndHashCode(of = {"id", "order", "productSku"})
@NamedEntityGraph(
        name = "OrderItem.graph.forShop",
        attributeNodes = {
                @NamedAttributeNode(value = "productSku", subgraph = "productSkuSubgraph"),
                @NamedAttributeNode("trackingHistory")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "productSkuSubgraph",
                        attributeNodes = {
                                @NamedAttributeNode("product"),
                                @NamedAttributeNode("attributes")
                        }
                )
        }
)
@Entity
@Table(name = "item_don_hang")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_item_don_hang", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_don_hang", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_phamsku", nullable = false)
    private ProductSku productSku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_don_hang_cua_hang", nullable = false)
    private ShopOrder shopOrder;

    @Column(name = "so_luong", nullable = false)
    private int quantity;

    @Column(name = "gia", nullable = false)
    private Long price;

    @Column(name = "thanh_cong", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean success = false;

    @Column(name = "ly_do_huy", columnDefinition = "TEXT")
    private String cancelReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "huy_boi")
    private CancelBy canceledBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai")
    private OrderTrackStatus status = OrderTrackStatus.PROCESSING;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<OrderItemTrack> trackingHistory = new HashSet<>();

    public void addTrackingHistory(OrderItemTrack track) {
        if (trackingHistory == null) trackingHistory = new HashSet<>();
        trackingHistory.add(track);
        track.setOrderItem(this);
    }


    public void setStatus(OrderTrackStatus status) {
        this.status = status;
        OrderItemTrack shopOrderTrack = new OrderItemTrack(this, status, LocalDateTime.now());
        this.addTrackingHistory(shopOrderTrack);
    }


}