package com.haiemdavang.AnrealShop.tech.gemini;

import java.util.Arrays;

public enum PromptType {
    PRODUCT_DESCRIPTION("product", "description"),
    SHOP_NAME("shop", "name"),
    SHOP_DESCRIPTION("shop", "description"),
    DEFAULT(null, null);

    private final String tableName;
    private final String fieldName;

    PromptType(String tableName, String fieldName) {
        this.tableName = tableName;
        this.fieldName = fieldName;
    }

    public static PromptType from(String tableName, String fieldName) {
        return Arrays.stream(values())
                .filter(t -> t.tableName.equals(tableName) && t.fieldName.equals(fieldName))
                .findFirst()
                .orElse(null);
    }
}
