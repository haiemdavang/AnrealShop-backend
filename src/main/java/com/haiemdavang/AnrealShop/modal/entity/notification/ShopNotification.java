package com.haiemdavang.AnrealShop.modal.entity.notification;


import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
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
@ToString(exclude = {"shop"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "thong_bao_cua_hang")
public class ShopNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_thong_bao_cua_hang", length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "noi_dung", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "anh_dai_dien")
    private String thumbnailUrl = "https://res.cloudinary.com/dlcjc36ow/image/upload/v1747916255/ImagError_jsv7hr.png";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cua_hang", nullable = false)
    private Shop shop;

    @Column(name = "duong_dan_chuyen_huong")
    private String redirectUrl;

    @Column(name = "da_doc", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isRead = false;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "ngay_cap_nhat")
    private LocalDateTime updatedAt;
}