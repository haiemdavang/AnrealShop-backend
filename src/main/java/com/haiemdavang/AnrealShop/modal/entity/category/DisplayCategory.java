package com.haiemdavang.AnrealShop.modal.entity.category;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.haiemdavang.AnrealShop.modal.enums.CategoryDisplayPosition;
import com.haiemdavang.AnrealShop.modal.enums.MediaType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "hien_thi_danh_muc")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisplayCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_hien_thi_danh_muc", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_danh_muc", nullable = false)
    private Category category;

    @Column(name = "anh_dai_dien")
    private String thumbnailUrl = "https://res.cloudinary.com/dlcjc36ow/image/upload/v1747916255/ImagError_jsv7hr.png";

    @Builder.Default
    @Column(name = "loai_media")
    @Enumerated(EnumType.STRING)
    private MediaType mediaType = MediaType.IMAGE;

    @Builder.Default
    @Column(name = "vi_tri")
    @Enumerated(EnumType.STRING)
    private CategoryDisplayPosition position = CategoryDisplayPosition.SIDEBAR;

    @Builder.Default
    @Column(name = "thu_tu_hien_thi", nullable = false)
    private int displayOrder = 0;
}