package com.haiemdavang.AnrealShop.tech.gemini;

public interface PromptStrategy {
    String buildPrompt(String context);
    PromptType getType();
}
