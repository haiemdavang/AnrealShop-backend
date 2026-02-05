package com.haiemdavang.AnrealShop.modal.entity.product;


import com.haiemdavang.AnrealShop.modal.enums.MediaType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "review")
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "danh_gia_san_pham_media")
public class ProductReviewMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_media_danh_gia_san_pham", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_gia_san_pham", nullable = false)
    private ProductReview review; // Đánh giá mà media này thuộc về

    @Column(name = "duong_dan_media", nullable = false, length = 255)
    private String mediaUrl; // URL của hình ảnh/video

    @Enumerated(EnumType.STRING) // Lưu tên Enum ("IMAGE", "VIDEO")
    @Column(name = "loai_media", length = 10, columnDefinition = "ENUM('IMAGE', 'VIDEO') DEFAULT 'IMAGE'")
    private MediaType mediaType = MediaType.IMAGE;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime createdAt;
}