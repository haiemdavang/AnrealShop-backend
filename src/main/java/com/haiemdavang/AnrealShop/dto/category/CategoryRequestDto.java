package com.haiemdavang.AnrealShop.dto.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryRequestDto {
    private String name;
    private String slug;
    private String parentId;
    private String description;
    private int level;
    private boolean visible;
}
