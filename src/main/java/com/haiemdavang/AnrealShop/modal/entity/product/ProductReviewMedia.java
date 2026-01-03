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
@Table(name = "product_review_media")
public class ProductReviewMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private ProductReview review; // Đánh giá mà media này thuộc về

    @Column(name = "media_url", nullable = false, length = 255)
    private String mediaUrl; // URL của hình ảnh/video

    @Enumerated(EnumType.STRING) // Lưu tên Enum ("IMAGE", "VIDEO")
    @Column(name = "media_type", length = 10, columnDefinition = "ENUM('IMAGE', 'VIDEO') DEFAULT 'IMAGE'")
    private MediaType mediaType = MediaType.IMAGE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}