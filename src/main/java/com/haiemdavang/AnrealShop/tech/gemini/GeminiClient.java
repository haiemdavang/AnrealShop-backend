package com.haiemdavang.AnrealShop.tech.gemini;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.OAuth2Credentials;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiClient {

    private final RestTemplate restTemplate;
    private final CredentialsProvider credentialsProvider;

    @Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @Value("${spring.cloud.gcp.location:us-central1}")
    private String location;

    @Value("${gemini.model:gemini-2.5-flash-lite}")
    private String model;

    public String generateText(String prompt) {
        try {
            String url = String.format(
                    "https://%s-aiplatform.googleapis.com/v1/projects/%s/locations/%s/publishers/google/models/%s:generateContent",
                    location,
                    projectId,
                    location,
                    model
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(getAccessToken());

            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("role", "user");
            content.put("parts", List.of(part));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(content));

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(requestBody, headers);

            ResponseEntity<GeminiResponseDto> response =
                    restTemplate.postForEntity(url, request, GeminiResponseDto.class);

            GeminiResponseDto body = response.getBody();

            if (body != null
                    && body.getCandidates() != null
                    && !body.getCandidates().isEmpty()
                    && body.getCandidates().get(0).getContent() != null
                    && body.getCandidates().get(0).getContent().getParts() != null
                    && !body.getCandidates().get(0).getContent().getParts().isEmpty()) {

                return body.getCandidates()
                        .get(0)
                        .getContent()
                        .getParts()
                        .get(0)
                        .getText();
            }

            return "No response from Gemini";

        } catch (Exception e) {
            throw new RuntimeException("Failed to call Gemini API: " + e.getMessage(), e);
        }
    }

    private String getAccessToken() throws IOException {
        Credentials credentials = credentialsProvider.getCredentials();

        if (!(credentials instanceof OAuth2Credentials oauth2Credentials)) {
            throw new RuntimeException("Credentials is not OAuth2Credentials");
        }

        oauth2Credentials.refreshIfExpired();

        AccessToken accessToken = oauth2Credentials.getAccessToken();

        if (accessToken == null || accessToken.getTokenValue() == null) {
            throw new RuntimeException("Failed to get Google access token");
        }

        return accessToken.getTokenValue();
    }
}