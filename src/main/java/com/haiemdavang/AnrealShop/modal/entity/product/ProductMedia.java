package com.haiemdavang.AnrealShop.modal.entity.product;

import com.haiemdavang.AnrealShop.modal.enums.MediaType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@Table(name = "media_san_pham")
public class ProductMedia {
    @Id
    @Column(name = "id_media_san_pham", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham", nullable = false)
    private Product product;

    @Column(name = "duong_dan", nullable = false, length = 255)
    private String url;

    @Column(name = "anh_dai_dien", nullable = false, length = 255)
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_media", nullable = false)
    private MediaType type = MediaType.IMAGE;
}
