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
@Table(name = "display_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisplayCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl = "https://res.cloudinary.com/dlcjc36ow/image/upload/v1747916255/ImagError_jsv7hr.png";

    @Builder.Default
    @Column(name = "media_type")
    @Enumerated(EnumType.STRING)
    private MediaType mediaType = MediaType.IMAGE;

    @Builder.Default
    @Column(name = "position")
    @Enumerated(EnumType.STRING)
    private CategoryDisplayPosition position = CategoryDisplayPosition.SIDEBAR;

    @Builder.Default
    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;
}