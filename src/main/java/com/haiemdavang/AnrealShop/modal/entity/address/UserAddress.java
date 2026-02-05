package com.haiemdavang.AnrealShop.modal.entity.address;

import com.haiemdavang.AnrealShop.modal.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "province", "district", "ward"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "dia_chi_nguoi_dung")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_dia_chi_nguoi_dung", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private User user;

    @Column(name = "nguoi_nhan", nullable = false, length = 100)
    private String receiverName;

    @Column(name = "so_dien_thoai", nullable = false, length = 20)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_tinh", nullable = false)
    private Province province;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_huyen", nullable = false)
    private District district;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_xa", nullable = false)
    private Ward ward;

    @Column(name = "chi_tiet", columnDefinition = "TEXT", nullable = false)
    private String detail;

    @Column(name = "dia_chi_mac_dinh", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean primaryAddress = false;

}