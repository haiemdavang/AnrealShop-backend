package com.haiemdavang.AnrealShop.tech.gemini.concreteStrategy;

import com.haiemdavang.AnrealShop.tech.gemini.PromptStrategy;
import com.haiemdavang.AnrealShop.tech.gemini.PromptType;
import org.springframework.stereotype.Component;

@Component
public class ShopDescriptionStrategy implements PromptStrategy {

    @Override
    public PromptType getType() {
        return PromptType.SHOP_DESCRIPTION;
    }

    @Override
    public String buildPrompt(String context) {
        return "Viết đoạn giới thiệu shop chuyên nghiệp, thân thiện cho: " + context;
    }
}
