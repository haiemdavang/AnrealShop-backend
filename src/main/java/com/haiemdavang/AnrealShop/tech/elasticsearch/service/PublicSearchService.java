package com.haiemdavang.AnrealShop.tech.elasticsearch.service;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.haiemdavang.AnrealShop.dto.search.CategorySuggestDto;
import com.haiemdavang.AnrealShop.dto.search.ProductSuggestDto;
import com.haiemdavang.AnrealShop.dto.search.publicSearchResponse;
import com.haiemdavang.AnrealShop.mapper.PublicSearchMapper;
import com.haiemdavang.AnrealShop.tech.elasticsearch.document.EsCategory;
import com.haiemdavang.AnrealShop.tech.elasticsearch.document.EsProduct;
import com.haiemdavang.AnrealShop.tech.elasticsearch.repository.EsCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicSearchService {
    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EsCategoryRepository esCategoryRepository;
    private final PublicSearchMapper publicSearchMapper;

    public publicSearchResponse suggestSearch(String keyword, int productLimit, int categoryLimit) {
        List<ProductSuggestDto> products = suggestProducts(keyword, productLimit);
        List<CategorySuggestDto> categories = suggestCategories(keyword, categoryLimit);

        return publicSearchResponse.builder()
                .products(products)
                .categories(categories)
                .build();
    }

    private List<ProductSuggestDto> suggestProducts(String keyword, int limit) {
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(QueryBuilders.bool(b -> {
                    b.filter(f -> f.term(t -> t.field("visible").value(true)));
                    b.filter(f -> f.term(t -> t.field("restrict_status").value("ACTIVE")));
                    b.should(QueryBuilders.matchPhrasePrefix(m ->
                            m.field("name.suggest").query(keyword)));
                    b.should(QueryBuilders.match(m ->
                            m.field("name.search_name").query(keyword)));
                    b.minimumShouldMatch("1");
                    return b;
                }))
                .withMaxResults(limit)
                .build();

        SearchHits<EsProduct> searchHits = elasticsearchTemplate.search(searchQuery, EsProduct.class);
        List<EsProduct> products = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList();

        Set<String> categoryIds = products.stream()
                .map(EsProduct::getCategoryId)
                .collect(Collectors.toSet());

        Map<String, String> categoryNameMap = esCategoryRepository.findByIdIn(categoryIds).stream()
                .collect(Collectors.toMap(EsCategory::getId, EsCategory::getName));

        return products.stream()
                .map(p -> publicSearchMapper.toProductSuggestDto(p, categoryNameMap.get(p.getCategoryId())))
                .toList();
    }

    private List<CategorySuggestDto> suggestCategories(String keyword, int limit) {
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(QueryBuilders.bool(b -> b
                        .should(QueryBuilders.matchPhrasePrefix(m ->
                                m.field("name.suggest").query(keyword)))
                        .should(QueryBuilders.match(m ->
                                m.field("name.search_name").query(keyword)))
                        .should(QueryBuilders.matchPhrasePrefix(m ->
                                m.field("url_path.suggest").query(keyword)))
                        .should(QueryBuilders.match(m ->
                                m.field("url_path.search_name").query(keyword)))
                        .minimumShouldMatch("1")
                ))
                .withMaxResults(limit)
                .build();

        SearchHits<EsCategory> searchHits = elasticsearchTemplate.search(searchQuery, EsCategory.class);

        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(publicSearchMapper::toCategorySuggestDto)
                .toList();
    }
}
