package com.haiemdavang.AnrealShop.modal.entity.product;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductAttributeId {

    @Column(name = "id_san_pham", length = 36)
    private String productId;

    @Column(name = "id_gia_tri_thuoc_tinh", length = 36)
    private String attributeValueId;
}