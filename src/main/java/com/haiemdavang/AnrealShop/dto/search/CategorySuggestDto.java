package com.haiemdavang.AnrealShop.dto.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySuggestDto {
    private String id;
    private String name;
    private String urlPath;
    private String urlSlug;
}
