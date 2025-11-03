package com.haiemdavang.AnrealShop.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.haiemdavang.AnrealShop.modal.enums.CategoryDisplayPosition;
import com.haiemdavang.AnrealShop.modal.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryDisplayDto {
    private String id;
    private String categoryId;
    private String categoryName;
    private CategoryDisplayPosition position;
    private int order;
    private String thumbnailUrl;
    private MediaType mediaType;
}
