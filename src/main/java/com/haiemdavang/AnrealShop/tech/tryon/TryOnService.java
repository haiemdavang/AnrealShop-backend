package com.haiemdavang.AnrealShop.tech.tryon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.CredentialsProvider; // <--- SỬ DỤNG CÁI NÀY
import com.google.cloud.aiplatform.v1.PredictResponse;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.google.protobuf.util.JsonFormat;
import com.haiemdavang.AnrealShop.dto.tryon.TryOnRequest;
import com.haiemdavang.AnrealShop.dto.tryon.TryOnResponse;
import com.haiemdavang.AnrealShop.exception.AnrealShopException;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TryOnService {

    private final ObjectMapper objectMapper;
    private final CredentialsProvider credentialsProvider;

    @org.springframework.beans.factory.annotation.Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @org.springframework.beans.factory.annotation.Value("${spring.cloud.gcp.credentials.location:us-central1}")
    private String location;

    private static final String MODEL_NAME = "virtual-try-on-001";

    public TryOnResponse tryOn(TryOnRequest request) {
        validateRequest(request);

        String endpoint = String.format("%s-aiplatform.googleapis.com:443", location);

        try {
            PredictionServiceSettings settings = PredictionServiceSettings.newBuilder()
                    .setEndpoint(endpoint)
                    .setCredentialsProvider(credentialsProvider)
                    .build();

            try (PredictionServiceClient client = PredictionServiceClient.create(settings)) {

                String name = String.format("projects/%s/locations/%s/publishers/google/models/%s",
                        projectId, location, MODEL_NAME);

                List<Value> instances = new ArrayList<>();
                Map<String, Object> instanceMap = buildInstanceMap(request);
                instances.add(mapToValue(instanceMap));

                Map<String, Object> parametersMap = new HashMap<>();
                parametersMap.put("baseSteps", request.getBaseSteps() != null ? request.getBaseSteps() : 25);
                Value parameters = mapToValue(parametersMap);

                PredictResponse response = client.predict(name, instances, parameters);

                return parseResponse(response);
            }

        } catch (IOException e) {
            throw new AnrealShopException("Connection error to Google Cloud: " + e.getMessage());
        } catch (Exception e) {
            throw new AnrealShopException("Virtual try-on processing failed: " + e.getMessage());
        }
    }

    private Map<String, Object> buildInstanceMap(TryOnRequest request) {
        String personImageBase64 = cleanBase64(request.getPersonImageBase64());
        String productImageBase64 = cleanBase64(request.getProductImageBase64());
        Map<String, Object> instance = new HashMap<>();
        instance.put("personImage", Map.of("image", Map.of("bytesBase64Encoded", personImageBase64)));
        instance.put("productImages", List.of(Map.of("image", Map.of("bytesBase64Encoded", productImageBase64))));
        return instance;
    }

    private TryOnResponse parseResponse(PredictResponse response) {
        if (response.getPredictionsCount() == 0) {
            throw new AnrealShopException("No predictions returned from AI Model");
        }
        try {
            Value prediction = response.getPredictions(0);
            Struct struct = prediction.getStructValue();
            String resultBase64 = struct.getFieldsOrThrow("bytesBase64Encoded").getStringValue();
            String mimeType = "image/png";
            if (struct.getFieldsMap().containsKey("mimeType")) {
                mimeType = struct.getFieldsMap().get("mimeType").getStringValue();
            }
            return TryOnResponse.success(resultBase64, mimeType);
        } catch (Exception e) {
            throw new AnrealShopException("Failed to parse AI response: " + e.getMessage());
        }
    }

    private Value mapToValue(Map<String, Object> map) throws InvalidProtocolBufferException, JsonProcessingException {
        String jsonString = objectMapper.writeValueAsString(map);
        Value.Builder valueBuilder = Value.newBuilder();
        JsonFormat.parser().merge(jsonString, valueBuilder);
        return valueBuilder.build();
    }

    private void validateRequest(TryOnRequest request) {
        if (request.getPersonImageBase64() == null || request.getPersonImageBase64().isBlank()) {
            throw new BadRequestException("PERSON_IMAGE_REQUIRED");
        }
        if (request.getProductImageBase64() == null || request.getProductImageBase64().isBlank()) {
            throw new BadRequestException("PRODUCT_IMAGE_REQUIRED");
        }
    }

    private String cleanBase64(String base64) {
        if (base64 == null) return null;
        if (base64.contains(",")) {
            return base64.substring(base64.indexOf(",") + 1);
        }
        return base64;
    }
}