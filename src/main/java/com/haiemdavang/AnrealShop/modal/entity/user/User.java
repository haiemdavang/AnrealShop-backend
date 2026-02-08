package com.haiemdavang.AnrealShop.modal.entity.user;

import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import com.haiemdavang.AnrealShop.modal.enums.GenderType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"role", "shops"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "nguoi_dung", indexes = {
        @Index(name = "idx_nguoidung_tendangnhap", columnList = "ten_dang_nhap", unique = true),
        @Index(name = "idx_nguoidung_email", columnList = "email", unique = true)
})
@SQLDelete(sql = "UPDATE nguoi_dung SET da_xoa = true, ngay_cap_nhat = CURRENT_TIMESTAMP WHERE id_nguoi_dung = ?")
@Where(clause = "da_xoa = false")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_nguoi_dung", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "ten_dang_nhap", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "mat_khau", length = 255)
    private String password;

    @Column(name = "ho_ten", length = 100)
    private String fullName;

    @Column(name = "so_dien_thoai", length = 20)
    private String phoneNumber;

    @Column(name = "anh_dai_dien", length = 255)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "gioi_tinh", columnDefinition = "ENUM('MALE', 'FEMALE', 'OTHER')")
    private GenderType gender;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_vai_tro")
    private Role role;

    @Column(name = "tu_mangxh", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean fromSocial = false;

    @Column(name = "ngay_sinh", columnDefinition = "DATE")
    private LocalDate dob;

    @Column(name = "da_xac_thuc", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean verify = false;

    @Column(name = "da_xoa", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean deleted = false;

    @Column(name = "ly_do_xoa", columnDefinition = "TEXT")
    private String deleteReason;


     @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
     private Set<Shop> shops;

    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private Set<UserAddress> userAddresses; // Địa chỉ của user
    // Lưu ý: Bảng user_addresses của bạn đang dùng username làm FK, nên cân nhắc đổi sang user_id (PK của users)
    // để các mối quan hệ JPA được tự nhiên hơn.

    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private Set<Order> orders; // Các đơn hàng của user

    // @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private Cart cart; // Giỏ hàng của user (nếu là OneToOne)

    // @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private Set<ProductReview> reviews; // Các đánh giá user đã viết

    // @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private Set<Notification> notifications; // Thông báo của user

    // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // private Set<Follow> follows; // Các shop user này theo dõi (nếu user theo dõi shop)

}