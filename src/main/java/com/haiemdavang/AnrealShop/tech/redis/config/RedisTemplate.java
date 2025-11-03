package com.haiemdavang.AnrealShop.tech.redis.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisTemplate {
    PREFIX_CART("user:%s:cart"),
    PREFIX_ADDRESS("user:%s:address"),
    PREFIX_USER("user:%s:user"),
    PREFIX_HAS_SHOP("user:%s:hasShop"),
    PREFIX_LIST_CATEGORY_DISPLAY_HOMEPAGE("categoryDisplay:homepage:%s");


    private final String value;
}
