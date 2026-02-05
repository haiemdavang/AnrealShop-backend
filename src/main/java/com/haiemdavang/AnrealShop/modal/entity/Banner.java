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
@Table(name = "banner")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_banner", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "duong_dan_anh", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "duong_dan_chuyen_huong", length = 255)
    private String redirectUrl;

    @Column(name = "tieu_de", length = 100) // Tiêu đề cho banner, có thể dùng cho SEO hoặc alt text
    private String title;

    @Column(name = "mo_ta", columnDefinition = "TEXT") // Mô tả ngắn về banner
    private String description;

    @Column(name = "dang_hoat_dong", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive = true; // Đánh dấu banner có đang hoạt động để hiển thị hay không

    @Column(name = "thu_tu_hien_thi", columnDefinition = "INT DEFAULT 0")
    private int displayOrder = 0; // Thứ tự hiển thị của banner (nếu có nhiều banner)

    @Column(name = "ngay_bat_dau")
    private LocalDateTime startDate; // Ngày bắt đầu hiển thị banner

    @Column(name = "ngay_ket_thuc")
    private LocalDateTime endDate; // Ngày kết thúc hiển thị banner

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime updatedAt;

}