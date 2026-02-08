package com.haiemdavang.AnrealShop.modal.entity.product;

import com.haiemdavang.AnrealShop.modal.entity.order.OrderItem;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "product", "orderItem", "mediaList"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "danh_gia_san_pham", indexes = {
        @Index(name = "idx_danhgiasanpham_user_product", columnList = "id_nguoi_dung, id_san_pham")
})
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_danh_gia_san_pham", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham", nullable = false)
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_item_don_hang", unique = true)
    private OrderItem orderItem; // Liên kết tới mục đơn hàng (để xác minh đã mua)

    @Column(name = "diem_danh_gia", nullable = false) // Ví dụ: 1, 2, 3, 4, 5
    private int rating; // Điểm đánh giá// Một OrderItem chỉ có một ProductReview

    @Column(name = "binh_luan", columnDefinition = "TEXT")
    private String comment; // Nội dung bình luận

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<ProductReviewMedia> mediaList = new ArrayList<>(); // Danh sách media đính kèm

    public void addMedia(ProductReviewMedia media) {
        if (mediaList == null) mediaList = new ArrayList<>();
        mediaList.add(media);
        media.setReview(this);
    }
}