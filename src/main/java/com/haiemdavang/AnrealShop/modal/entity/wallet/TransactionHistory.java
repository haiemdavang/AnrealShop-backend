package com.haiemdavang.AnrealShop.modal.entity.wallet;

import com.haiemdavang.AnrealShop.modal.enums.TransactionStatus;
import com.haiemdavang.AnrealShop.modal.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "lich_su_giao_dich_vi")
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_vi", nullable = false,
            foreignKey = @ForeignKey(name = "fk_giaodich_vi"))
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_giao_dich", nullable = false, length = 30)
    private TransactionType transactionType;

    @Column(name = "so_tien", nullable = false)
    private long amount;

    @Column(name = "so_du_truoc", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private long balanceBefore;

    @Column(name = "so_du_sau", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private long balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'THANH_CONG'")
    private TransactionStatus status = TransactionStatus.THANH_CONG;

    @Column(name = "ma_tham_chieu", length = 100)
    private String referenceCode;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
