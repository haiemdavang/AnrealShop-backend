package com.haiemdavang.AnrealShop.modal.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user"})
@EqualsAndHashCode(of = "id")
@Table(name = "lich_su_dang_nhap", indexes = {
        @Index(name = "idx_lichsudangnhap_nguoidung", columnList = "id_nguoi_dung"),
        @Index(name = "idx_lichsudangnhap_thoigiandangnhap", columnList = "thoi_gian_dang_nhap")
})
public class HistoryLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_lich_su_dang_nhap", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(name = "thoi_gian_dang_nhap", nullable = false)
    private LocalDateTime loginAt;

    @Column(name = "thoi_gian_dang_xuat")
    private LocalDateTime logoutAt;

    @Column(name = "dia_chi_ip", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "vi_tri", length = 100)
    private String location;

    @Column(name = "thiet_bi", length = 100, unique = true)
    private String device;
}
