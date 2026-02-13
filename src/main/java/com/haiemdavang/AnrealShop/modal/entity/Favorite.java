package com.haiemdavang.AnrealShop.modal.entity;

import com.haiemdavang.AnrealShop.modal.entity.product.Product;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "product"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "yeu_thich",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_product_favorite", columnNames = {"id_nguoi_dung", "id_san_pham"})
        },
        indexes = {
                @Index(name = "idx_favorite_user", columnList = "id_nguoi_dung"),
                @Index(name = "idx_favorite_product", columnList = "id_san_pham")
        }
)
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_yeu_thich", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham", nullable = false)
    private Product product;

    @CreationTimestamp
    @Column(name = "thoi_gian_yeu_thich", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
