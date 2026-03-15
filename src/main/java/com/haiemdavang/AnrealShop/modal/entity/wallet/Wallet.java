package com.haiemdavang.AnrealShop.modal.entity.wallet;

import com.haiemdavang.AnrealShop.modal.enums.WalletOwnerType;
import com.haiemdavang.AnrealShop.modal.enums.WalletStatus;
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
@Table(name = "vi_dien_tu",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_vi_chu_so_huu", columnNames = {"ma_chu_so_huu", "loai_chu_so_huu"})
        }
)
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "ma_chu_so_huu", nullable = false, length = 36)
    private String ownerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_chu_so_huu", nullable = false, length = 20)
    private WalletOwnerType ownerType;

    @Column(name = "so_du_kha_dung", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private long availableBalance;

    @Column(name = "loai_tien_te", length = 10, columnDefinition = "VARCHAR(10) DEFAULT 'VND'")
    private String currency = "VND";

    @Column(name = "mat_khau_thanh_toan", length = 255)
    private String paymentPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'DANG_HOAT_DONG'")
    private WalletStatus status = WalletStatus.DANG_HOAT_DONG;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime updatedAt;
}
