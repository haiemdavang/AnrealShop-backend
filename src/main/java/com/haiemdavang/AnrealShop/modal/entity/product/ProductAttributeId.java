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

    @Column(name = "ma_san_pham", length = 36)
    private String productId;

    @Column(name = "ma_gia_tri_thuoc_tinh", length = 36)
    private String attributeValueId;
}