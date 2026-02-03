package com.haiemdavang.AnrealShop.modal.entity;
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
@ToString
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "banners")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "redirect_url", length = 255)
    private String redirectUrl;

    @Column(name = "title", length = 100) // Tiêu đề cho banner, có thể dùng cho SEO hoặc alt text
    private String title;

    @Column(name = "description", columnDefinition = "TEXT") // Mô tả ngắn về banner
    private String description;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive = true; // Đánh dấu banner có đang hoạt động để hiển thị hay không

    @Column(name = "display_order", columnDefinition = "INT DEFAULT 0")
    private int displayOrder = 0; // Thứ tự hiển thị của banner (nếu có nhiều banner)

    @Column(name = "start_date")
    private LocalDateTime startDate; // Ngày bắt đầu hiển thị banner

    @Column(name = "end_date")
    private LocalDateTime endDate; // Ngày kết thúc hiển thị banner

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}