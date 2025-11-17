package com.haiemdavang.AnrealShop.tech.elasticsearch.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.json.JsonData;
import com.haiemdavang.AnrealShop.dto.product.EsProductDto;
import com.haiemdavang.AnrealShop.tech.elasticsearch.document.EsCategory;
import com.haiemdavang.AnrealShop.tech.elasticsearch.document.EsProduct;
import com.haiemdavang.AnrealShop.tech.elasticsearch.repository.EsCategoryRepository;
import com.haiemdavang.AnrealShop.tech.elasticsearch.repository.EsProductRepository;
import com.haiemdavang.AnrealShop.exception.AnrealShopException;
import com.haiemdavang.AnrealShop.mapper.ProductMapper;
import com.haiemdavang.AnrealShop.modal.enums.RestrictStatus;
import com.haiemdavang.AnrealShop.utils.ApplicationInitHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductIndexerService {
    private final EsProductRepository esProductRepository;
    private final EsCategoryRepository esCategoryRepository;
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ProductMapper productMapper;

    @Transactional
    public void indexProduct(EsProductDto message) {
        EsProduct esProduct =  productMapper.toEsProduct(message);
        esProductRepository.save(esProduct);
    }

    public List<EsProduct> suggestMyProductsName(String keyword, String id) {
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(QueryBuilders.bool(b -> {
                            b.should(QueryBuilders.matchPhrasePrefix(m ->
                                    m.field("name.suggest").query(keyword)));
                            b.should(QueryBuilders.match(m ->
                                    m.field("name.search_name").query(keyword)));

                            b.minimumShouldMatch("1");

                            if (id != null && !id.isBlank()) {
                                b.filter(f -> f.term(t -> t.field("shop.id").value(id)));
                            }
                            return b;
                        }
                ))
                .withMaxResults(10)
                .build();

        SearchHits<EsProduct> searchHit = elasticsearchTemplate.search(searchQuery, EsProduct.class);
        return searchHit.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();
    }

    @Transactional
    public void deleteProductFromIndex(String id) {
        esProductRepository.deleteById(id);
    }

    @Transactional
    public void deleteProductFromIndex(Set<String> id) {
        esProductRepository.deleteByIdIn(id);
    }

    @Transactional
    public void updateProductVisibility(String id, boolean visible) {
        EsProduct esProduct = esProductRepository.findById(id)
                .orElseThrow(() -> new AnrealShopException("ES_PRODUCT_NOT_FOUND"));
        esProduct.setVisible(visible);
        esProduct.setUpdatedAt(Instant.now());
        esProductRepository.save(esProduct);
    }

    @Transactional
    public void updateProductVisibility(Set<String> ids, boolean visible) {
        Set<EsProduct> esProducts = esProductRepository.findByIdIn(ids);
        esProducts.forEach(esProduct -> {
            esProduct.setVisible(visible);
            esProduct.setUpdatedAt(Instant.now());
        });
        esProductRepository.saveAll(esProducts);
    }

    public List<EsProductDto> searchProducts(int page, int limit, String search, String categoryId, String sortBy, Double minPrice, Double maxPrice, Integer rating, List<String> brands, List<String> colors, List<String> sizes, List<String> origins, List<String> genders) {
        var queryBuilder = NativeQuery.builder();

        queryBuilder.withQuery(QueryBuilders.bool(b -> {
            b.filter(f -> f.term(t -> t.field("visible").value(true)));
            b.filter(f -> f.term(t -> t.field("restrict_status").value("ACTIVE")));

            if (search != null && !search.trim().isEmpty()) {
                b.must(m -> m.multiMatch(mm ->
                        mm.query(search)
                                .fields("name", "sort_description")
                                .type(TextQueryType.BestFields)
                ));
            }else {
                b.must(m -> m.matchAll(a -> a));
            }

            if (categoryId != null && !categoryId.trim().isEmpty()) {
                b.filter(f -> f.term(t -> t.field("category_id").value(categoryId)));
            }

            if (minPrice != null || maxPrice != null) {
                b.filter(f -> f.range(r -> r.number(v -> v.field("discount_price").gte(minPrice).lte(maxPrice))));
            }

            if (rating != null && rating > 0) {
                b.filter(f -> f.range(r -> r.number(v -> v.field("average_rating").gte(Double.valueOf(rating)))));
            }

            addNestedAttributeFilter(b, "BRAND", brands);
            addNestedAttributeFilter(b, "COLOR", colors);
            addNestedAttributeFilter(b, "SIZE", sizes);
            addNestedAttributeFilter(b, "ORIGIN", origins);
            addNestedAttributeFilter(b, "TARGET_AUDIENCE", genders);

            return b;
        }));

        queryBuilder.withPageable(PageRequest.of(page, limit, ApplicationInitHelper.getSortBy(sortBy)));

        SearchHits<EsProduct> searchHits = elasticsearchTemplate.search(queryBuilder.build(), EsProduct.class);

        Set<String> cateIds = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(EsProduct::getCategoryId)
                .collect(Collectors.toSet());

        Set<EsCategory> categories = esCategoryRepository.findByIdIn(cateIds);
        Map<String, EsCategory> categoryMap = categories.stream()
                .collect(Collectors.toMap(EsCategory::getId, category -> category));


        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(esProduct -> {
                    EsCategory category = categoryMap.get(esProduct.getCategoryId());
                    return productMapper.toEsProductDto(esProduct, category);
                })
                .collect(Collectors.toList());
    }


    public void updateProductStatus(String id, RestrictStatus status) {
        EsProduct esProduct = esProductRepository.findById(id)
                .orElseThrow(() -> new AnrealShopException("ES_PRODUCT_NOT_FOUND"));
        if (status == RestrictStatus.VIOLATION) {
            esProduct.setVisible(false);
        } else if (status == RestrictStatus.ACTIVE) {
            esProduct.setVisible(true);
        }
        esProduct.setUpdatedAt(Instant.now());
        esProduct.setRestrictStatus(status.getId());
        esProductRepository.save(esProduct);
    }

    private void addNestedAttributeFilter(BoolQuery.Builder b, String keyName, List<String> values) {
        if (values == null || values.isEmpty()) {
            return;
        }

        b.filter(f -> f.nested(n -> n
                .path("attributes")
                .query(q -> q.bool(bb -> bb
                        .must(m -> m.term(t -> t.field("attributes.keyName").value(keyName)))
                        .must(m -> m.terms(t -> t
                                .field("attributes.value")
                                .terms(tt -> tt.value(values.stream()
                                        .map(co.elastic.clients.elasticsearch._types.FieldValue::of)
                                        .collect(Collectors.toList())))))
                ))
        ));
    }
}
