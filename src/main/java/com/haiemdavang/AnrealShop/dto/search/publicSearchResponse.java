package com.haiemdavang.AnrealShop.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class publicSearchResponse {
    private List<ProductSuggestDto> products;
    private List<CategorySuggestDto> categories;
}
