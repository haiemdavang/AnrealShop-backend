package com.haiemdavang.AnrealShop.service.serviceInter;

import com.haiemdavang.AnrealShop.dto.attribute.AttributeResponse;
import com.haiemdavang.AnrealShop.dto.attribute.ProductAttributeDto;
import com.haiemdavang.AnrealShop.modal.entity.attribute.AttributeValue;

import java.util.List;
import java.util.Set;

public interface IAttributeService {
    AttributeResponse getAttributesForShop();

    Set<AttributeValue> getAttributeValues(List<ProductAttributeDto> attributes);
}
