package com.haiemdavang.AnrealShop.service.serviceImp;

import com.haiemdavang.AnrealShop.dto.attribute.AttributeDtoDto;
import com.haiemdavang.AnrealShop.dto.attribute.AttributeResponse;
import com.haiemdavang.AnrealShop.dto.attribute.ProductAttributeDto;
import com.haiemdavang.AnrealShop.mapper.AttributeMapper;
import com.haiemdavang.AnrealShop.modal.entity.shop.Shop;
import com.haiemdavang.AnrealShop.modal.entity.attribute.AttributeKey;
import com.haiemdavang.AnrealShop.modal.entity.attribute.AttributeValue;
import com.haiemdavang.AnrealShop.repository.AttributeKeyRepository;
import com.haiemdavang.AnrealShop.repository.AttributeValueRepository;
import com.haiemdavang.AnrealShop.repository.ShopRepository;
import com.haiemdavang.AnrealShop.security.SecurityUtils;
import com.haiemdavang.AnrealShop.service.serviceInter.IAttributeService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttributeServiceImp implements IAttributeService {

    private final SecurityUtils securityUtils;
    private final ShopRepository shopRepository;
    private final AttributeKeyRepository attributeKeyRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final AttributeMapper attributeMapper;

    
    @Override
    public AttributeResponse getAttributesForShop() {
        Shop shop = securityUtils.getCurrentUserShop();
        List<AttributeKey> attributeKeys = attributeKeyRepository.findAllByIsDefaultTrueOrShopsContains(shop);
        return createAttributeResponses(attributeKeys);
    }

    @Override
    public Set<AttributeValue> getAttributeValues(List<ProductAttributeDto> attributes) {
        Set<Pair<String, String>> expectedPairs = attributes.stream()
                .flatMap(attr -> attr.getValues().stream()
                        .map(value -> Pair.of(attr.getAttributeKeyName(), value)))
                .collect(Collectors.toSet());

        Set<String> newKeyNames = expectedPairs.stream()
                .map(Pair::getLeft)
                .collect(Collectors.toSet());
        Set<String> newValues = expectedPairs.stream()
                .map(Pair::getRight)
                .collect(Collectors.toSet());
        Set<AttributeValue> matchedAttributeValues = attributeValueRepository
                .findByAttributeKeyKeyNameInAndValueIn(newKeyNames, newValues);
        return matchedAttributeValues.stream()
                .filter(av -> expectedPairs.contains(Pair.of(av.getAttributeKey().getKeyName(), av.getValue())))
                .collect(Collectors.toSet());
    }


    private AttributeResponse createAttributeResponses(List<AttributeKey> attributeKeys) {
        List<AttributeValue> allAttributeValues = attributeValueRepository.findByAttributeKeyIn(attributeKeys);

        Map<AttributeKey, List<AttributeValue>> attributeMap = allAttributeValues.stream()
                .collect(Collectors.groupingBy(AttributeValue::getAttributeKey));

        List<AttributeDtoDto> attributeDtoList = new ArrayList<>();
        List<ProductAttributeDto> productAttributeDtos = new ArrayList<>();

        attributeKeys.forEach(key -> {
            List<AttributeValue> values = attributeMap.getOrDefault(key, new ArrayList<>());
            if (key.isForSku()) {
                productAttributeDtos.add(attributeMapper.toProductAttribute(key, values));
            } else {
                attributeDtoList.add(attributeMapper.toAttributeDto(key, values));
            }
        });
        return AttributeResponse.builder()
                .attribute(attributeDtoList)
                .attributeForSku(productAttributeDtos)
                .build();
    }
}
