package com.haiemdavang.AnrealShop.modal.entity.order;


import com.haiemdavang.AnrealShop.modal.entity.address.UserAddress;
import com.haiemdavang.AnrealShop.modal.entity.shop.ShopOrder;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.modal.enums.OrderStatus;
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
@ToString(exclude = {"user", "shippingAddress", "payment", "orderItems", "shopOrders"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "don_hang")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_don_hang", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dia_chi_nguoi_dung", nullable = false)
    private UserAddress shippingAddress;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "id_thanh_toan", referencedColumnName = "id_thanh_toan")
    private Payment payment;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime updatedAt;

    @Builder.Default
     @Enumerated(EnumType.STRING)
     @Column(name = "trang_thai", nullable = false)
     private OrderStatus status = OrderStatus.PROCESSING;

     @Column(name = "tam_tinh")
     private Long subTotalAmount;

     @Column(name = "tong_phi_van_chuyen")
     private Long totalShippingFee;

     @Column(name = "tong_tien", nullable = false)
     private Long grandTotalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<OrderItem> orderItems = new HashSet<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ShopOrder> shopOrders = new HashSet<>();


    public void addOrderItem(OrderItem item) {
        if (orderItems == null) {
            orderItems = new HashSet<>();
        }
        orderItems.add(item);
        item.setOrder(this);
    }

    public void removeOrderItem(OrderItem item) {
        if (orderItems != null) {
            orderItems.remove(item);
            item.setOrder(null);
        }
    }
    public void addShopOrder(ShopOrder shopOrder) {
        if (shopOrders == null) {
            shopOrders = new HashSet<>();
        }
        shopOrders.add(shopOrder);
        shopOrder.setOrder(this);
    }

    public void removeShopOrder(ShopOrder shopOrder) {
        if (shopOrders != null) {
            shopOrders.remove(shopOrder);
            shopOrder.setOrder(null);
        }
    }
}