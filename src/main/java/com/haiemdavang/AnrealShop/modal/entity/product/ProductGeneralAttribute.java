package com.haiemdavang.AnrealShop.modal.entity.product;
import com.haiemdavang.AnrealShop.modal.entity.attribute.AttributeValue;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "san_pham_thuoc_tinh_chung")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductGeneralAttribute {
    @EqualsAndHashCode.Include
    @EmbeddedId
    private ProductAttributeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "id_san_pham", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("attributeValueId")
    @JoinColumn(name = "id_gia_tri_thuoc_tinh", nullable = false)
    private AttributeValue attributeValue;
}
