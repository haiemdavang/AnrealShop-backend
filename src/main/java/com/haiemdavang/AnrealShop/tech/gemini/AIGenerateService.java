package com.haiemdavang.AnrealShop.tech.gemini;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AIGenerateService {
    private final GeminiClient geminiClient;
    private final PromptStrategyFactory factory;

    public String generate(String tableName, String fieldName, String context) {
        PromptStrategy strategy = factory.getStrategy(tableName, fieldName);
        String prompt = strategy.buildPrompt(context);
        return geminiClient.generateText(prompt);
    }
}
