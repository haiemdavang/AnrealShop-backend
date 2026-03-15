package com.haiemdavang.AnrealShop.modal.entity.order;

// Import các Enums và User nếu cần liên kết trực tiếp (ví dụ: người thực hiện thanh toán)
import com.haiemdavang.AnrealShop.modal.enums.PaymentGateway;
import com.haiemdavang.AnrealShop.modal.enums.PaymentStatus;
import com.haiemdavang.AnrealShop.modal.enums.PaymentType; // Nếu dùng Enum cho type
import com.haiemdavang.AnrealShop.modal.entity.order.Order; // Nếu có quan hệ ngược lại

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "thanh_toan")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_thanh_toan", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "so_tien", nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "cong_thanh_toan", columnDefinition = "ENUM('VNPAY', 'CASH_ON_DELIVERY', 'MOMO')")
    private PaymentGateway gateway;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_thanh_toan", length = 50)
    private PaymentType type;


    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "trang_thai", nullable = false, columnDefinition = "ENUM('PENDING', 'COD', 'COMPLETED', 'EXPIRED', 'CANCELLED', 'REFUNDED', 'FAILED') DEFAULT 'PENDING'")
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "het_han_luc")
    private LocalDateTime expireAt;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime updatedAt;


    // @OneToMany(mappedBy = "payment", fetch = FetchType.LAZY)
    // private Set<Order> orders;
}