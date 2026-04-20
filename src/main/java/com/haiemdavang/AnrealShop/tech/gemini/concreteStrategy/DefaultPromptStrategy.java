package com.haiemdavang.AnrealShop.tech.gemini.concreteStrategy;

import com.haiemdavang.AnrealShop.tech.gemini.PromptStrategy;
import com.haiemdavang.AnrealShop.tech.gemini.PromptType;
import org.springframework.stereotype.Component;

@Component
public class DefaultPromptStrategy implements PromptStrategy {


    @Override
    public String buildPrompt(String context) {
        return "Hãy gợi ý nội dung phù hợp cho: " + context;
    }

    @Override
    public PromptType getType() {
        return PromptType.DEFAULT;
    }
}
