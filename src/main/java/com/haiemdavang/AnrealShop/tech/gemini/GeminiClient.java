package com.haiemdavang.AnrealShop.tech.gemini;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiClient {

    private final RestTemplate restTemplate;

    @Value("${spring.ai.google.genai.api-key:}")
    private String apiKey;

    @Value("${spring.ai.google.genai.chat.options.model:gemini-2.5-flash-lite}")
    private String model;

    public GeminiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateText(String prompt) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new RuntimeException("Gemini API Key is missing");
        }

        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> parts = new HashMap<>();
        parts.put("text", prompt);

        Map<String, Object> contents = new HashMap<>();
        contents.put("parts", List.of(parts));

        requestBody.put("contents", List.of(contents));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<GeminiResponseDto> response = restTemplate.postForEntity(url, request, GeminiResponseDto.class);

        if (response.getBody() != null && response.getBody().getCandidates() != null
                && !response.getBody().getCandidates().isEmpty()) {
            return response.getBody().getCandidates().get(0).getContent().getParts().get(0).getText();
        }

        return "No response from Gemini";
    }
}
