package com.haiemdavang.AnrealShop.modal.entity.cart;

import com.haiemdavang.AnrealShop.modal.entity.product.Product;
import com.haiemdavang.AnrealShop.modal.entity.attribute.AttributeValue;
import com.haiemdavang.AnrealShop.modal.entity.product.ProductSku;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"cart", "productSku"})
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "gio_hang_item")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_gio_hang_item", length = 36, updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_gio_hang", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_phamsku", nullable = false)
    private ProductSku productSku;

    @Column(name = "gia", nullable = false)
    private Long price;

    @Column(name = "so_luong", columnDefinition = "INT DEFAULT 1")
    private int quantity = 1;

    @Column(name = "duoc_chon", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean selected = true;


}