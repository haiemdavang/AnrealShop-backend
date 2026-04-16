package com.haiemdavang.AnrealShop.dto.shop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopUpdateRequest {
    private String name;
    private String description;
    private String urlSlug;
    private String avatarUrl;
}


