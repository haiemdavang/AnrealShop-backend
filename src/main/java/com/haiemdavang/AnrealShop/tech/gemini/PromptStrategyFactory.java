package com.haiemdavang.AnrealShop.tech.gemini;

import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.tech.gemini.concreteStrategy.DefaultPromptStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PromptStrategyFactory {
    private final Map<PromptType, PromptStrategy> strategies;
    private final DefaultPromptStrategy defaultPromptStrategy;

    public PromptStrategyFactory(List<PromptStrategy> strategies, DefaultPromptStrategy defaultPromptStrategy) {
        this.defaultPromptStrategy = defaultPromptStrategy;
        this.strategies = strategies.stream()
                .filter(s -> !(s instanceof DefaultPromptStrategy))
                .collect(Collectors.toMap(PromptStrategy::getType, Function.identity()));
    }

    public PromptStrategy getStrategy(String tableName, String fieldName) {
        try {
            PromptType type = PromptType.from(tableName, fieldName);
            if (type == null) return defaultPromptStrategy;
            return strategies.getOrDefault(type, defaultPromptStrategy);
        } catch (Exception e) {
            throw new BadRequestException("FIELD_NOTFOUND");
        }
    }
}
