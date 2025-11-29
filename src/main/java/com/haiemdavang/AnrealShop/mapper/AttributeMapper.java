package com.haiemdavang.AnrealShop.mapper;

import com.haiemdavang.AnrealShop.dto.attribute.AttributeDtoDto;
import com.haiemdavang.AnrealShop.dto.attribute.ProductAttributeDto;
import com.haiemdavang.AnrealShop.dto.attribute.ProductAttributeSingleValueDto;
import com.haiemdavang.AnrealShop.tech.elasticsearch.document.EsAttribute;
import com.haiemdavang.AnrealShop.modal.entity.attribute.AttributeKey;
import com.haiemdavang.AnrealShop.modal.entity.attribute.AttributeValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class AttributeMapper {
    public List<ProductAttributeDto> toProductAttributeDtoFromEs(List<EsAttribute> attributes) {
        return attributes.stream()
                .map(attribute -> ProductAttributeDto.builder()
                        .attributeKeyName(attribute.getKeyName())
                        .attributeKeyDisplay(attribute.getDisplayName())
                        .values(attribute.getValue())
                        .build())
                .collect(Collectors.toList());
    }
    public List<ProductAttributeDto> toProductAttributeDto(List<ProductAttributeSingleValueDto> attributeSingleValueDtos) {
        return attributeSingleValueDtos.stream()
                .collect(Collectors.groupingBy(
                        ProductAttributeSingleValueDto::getAttributeKeyName,
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(enty -> {
                    String keyName = enty.getKey();
                    List<ProductAttributeSingleValueDto> values = enty.getValue();
                    String displayName = values.get(0).getAttributeKeyDisplay();
                    List<String> valueList = values.stream()
                            .map(ProductAttributeSingleValueDto::getValues)
                            .toList();
                    return new ProductAttributeDto(keyName, displayName, valueList);
                }).toList();
    }

    private EsAttribute toEsAttribute(ProductAttributeDto productAttributeDto) {
        if (productAttributeDto == null) {
            return null;
        }

        return EsAttribute.builder()
                .keyName(productAttributeDto.getAttributeKeyName())
                .displayName(productAttributeDto.getAttributeKeyDisplay())
                .value(productAttributeDto.getValues())
                .build();
    }
    public List<EsAttribute> toEsAttributes(List<ProductAttributeDto> productAttributeDtos) {
        if (productAttributeDtos == null || productAttributeDtos.isEmpty()) {
            return new ArrayList<>();
        }

        return productAttributeDtos.stream()
                .map(this::toEsAttribute)
                .toList();
    }
    

    public ProductAttributeDto toProductAttribute(AttributeKey attributeKey, List<AttributeValue> attributeValues) {
        List<String> values = attributeValues.stream()
                .map(AttributeValue::getValue)
                .collect(Collectors.toList());
                
        return ProductAttributeDto.builder()
                .attributeKeyName(attributeKey.getKeyName())
                .attributeKeyDisplay(attributeKey.getDisplayName())
                .values(values)
                .build();
    }
    
    public AttributeDtoDto toAttributeDto(AttributeKey attributeKey, List<AttributeValue> attributeValues) {
        List<String> values = attributeValues.stream()
                .map(AttributeValue::getValue)
                .collect(Collectors.toList());
                
        return AttributeDtoDto.builder()
                .attributeKeyName(attributeKey.getKeyName())
                .attributeKeyDisplay(attributeKey.getDisplayName())
                .values(values)
                .displayOrder(attributeKey.getDisplayOrder())
                .isDefault(attributeKey.isDefault())
                .isMultiSelect(attributeKey.isMultiSelected())
                .build();
    }


    public List<ProductAttributeDto> formatAttributes(List<ProductAttributeDto> attributeList) {
        Map<String, ProductAttributeDto> grouped = attributeList.stream()
                .collect(Collectors.toMap(
                        attr -> attr.getAttributeKeyName() + "::" + attr.getAttributeKeyDisplay(),
                        attr -> ProductAttributeDto.builder()
                                .attributeKeyName(attr.getAttributeKeyName())
                                .attributeKeyDisplay(attr.getAttributeKeyDisplay())
                                .values(new ArrayList<>(attr.getValues()))
                                .build(),
                        (a, b) -> {
                            a.getValues().addAll(b.getValues());
                            return a;
                        }
                ));

        return grouped.values().stream()
                .map(attribute -> {
                    List<String> cleaned = attribute.getValues().stream()
                            .map(value -> value.replaceAll("\\s+", " ").trim())
                            .distinct()
                            .collect(Collectors.toList());
                    return ProductAttributeDto.builder()
                            .attributeKeyName(attribute.getAttributeKeyName())
                            .attributeKeyDisplay(attribute.getAttributeKeyDisplay())
                            .values(cleaned)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<ProductAttributeDto> toProductAttributeDtoFromAttributeValue(List<AttributeValue> attributeValues) {
        if (attributeValues == null || attributeValues.isEmpty()) {
            return new ArrayList<>();
        }
        List<ProductAttributeDto> resutl =  new ArrayList<>();
        for (AttributeValue attributeValue : attributeValues) {
            ProductAttributeDto productAttributeDto = ProductAttributeDto.builder()
                    .attributeKeyName(attributeValue.getAttributeKey().getKeyName())
                    .attributeKeyDisplay(attributeValue.getAttributeKey().getDisplayName())
                    .values(List.of(attributeValue.getValue()))
                    .build();
            resutl.add(productAttributeDto);
        }
        return resutl;
    }

    public String getAttribteString(Set<AttributeValue> attributeValues) {
        if (attributeValues != null) {
            return attributeValues.stream()
                    .map(AttributeValue::getValue)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
        }
        return "";
    }

    public List<ProductAttributeSingleValueDto> toProductAttributeSingleValueDto(Set<AttributeValue> attributes) {
        return attributes.stream()
                .map(attribute -> ProductAttributeSingleValueDto.builder()
                        .attributeKeyName(attribute.getAttributeKey().getKeyName())
                        .attributeKeyDisplay(attribute.getAttributeKey().getDisplayName())
                        .values(attribute.getValue())
                        .build())
                .collect(Collectors.toList());
    }


}
