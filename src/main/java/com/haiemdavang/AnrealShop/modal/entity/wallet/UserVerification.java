package com.haiemdavang.AnrealShop.modal.entity.wallet;

import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.modal.enums.DocumentType;
import com.haiemdavang.AnrealShop.modal.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "xac_thuc_nguoi_dung",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_so_giay_to", columnNames = {"so_giay_to"})
        }
)
public class UserVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nguoi_dung", nullable = false,
            foreignKey = @ForeignKey(name = "fk_xacthuc_nguoidung"))
    private User user;

    @Column(name = "ho_ten_that", nullable = false, length = 100)
    private String realFullName;

    @Column(name = "so_giay_to", nullable = false, unique = true, length = 20)
    private String documentNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_giay_to", nullable = false, length = 20)
    private DocumentType documentType;

    @Column(name = "ngay_sinh")
    private LocalDate dateOfBirth;

    @Column(name = "anh_mat_truoc", length = 255)
    private String frontImageUrl;

    @Column(name = "anh_mat_sau", length = 255)
    private String backImageUrl;

    @Column(name = "anh_chan_dung", length = 255)
    private String portraitImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'CHO_DUYET'")
    private VerificationStatus status = VerificationStatus.CHO_DUYET;

    @Column(name = "ly_do_tu_choi", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "ngay_phe_duyet")
    private LocalDateTime approvedAt;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime updatedAt;
}
