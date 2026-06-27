package com.haiemdavang.AnrealShop.tech.rag;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.aiplatform.v1.EndpointName;
import com.google.cloud.aiplatform.v1.PredictRequest;
import com.google.cloud.aiplatform.v1.PredictResponse;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1.PredictionServiceSettings;
import com.google.protobuf.ListValue;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.haiemdavang.AnrealShop.dto.product.EsProductDto;
import com.haiemdavang.AnrealShop.exception.AnrealShopException;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RAGService {

    private final CredentialsProvider credentialsProvider;

    @org.springframework.beans.factory.annotation.Value("${spring.cloud.gcp.project-id}")
    private String projectId;

    @org.springframework.beans.factory.annotation.Value("${spring.cloud.gcp.location:us-central1}")
    private String location;

    @org.springframework.beans.factory.annotation.Value("${gemini.embedding-model:gemini-embedding-001}")
    private String embeddingModel;

    @org.springframework.beans.factory.annotation.Value("${gemini.embedding-dimension:768}")
    private Integer embeddingDimension;

    public List<Float> convertToVector(String rawInput) {
        return convertToVector(rawInput, "RETRIEVAL_QUERY");
    }

    public List<Float> convertProductToVector(EsProductDto product) {
        if (product == null) {
            throw new BadRequestException("PRODUCT_EMBEDDING_DATA_REQUIRED");
        }

        List<String> productContent = new ArrayList<>();
        addContent(productContent, "Tên sản phẩm", product.getName());
        addContent(productContent, "Mô tả ngắn", product.getSortDescription());
        addContent(productContent, "Mô tả", product.getDescription());
        addContent(productContent, "Giá gốc", product.getPrice());
        addContent(productContent, "Giá giảm", product.getDiscountPrice());
        addContent(productContent, "Danh mục", product.getCategory());
        addContent(productContent, "Thuộc tính", product.getAttributes());

        return convertToVector(String.join("\n", productContent), "RETRIEVAL_DOCUMENT");
    }

    private List<Float> convertToVector(String rawInput, String taskType) {
        if (rawInput == null || rawInput.isBlank()) {
            throw new BadRequestException("EMBEDDING_INPUT_REQUIRED");
        }

        try {
            String endpoint = location + "-aiplatform.googleapis.com:443";

            PredictionServiceSettings settings =
                    PredictionServiceSettings.newBuilder()
                            .setEndpoint(endpoint)
                            .setCredentialsProvider(credentialsProvider)
                            .build();

            try (PredictionServiceClient client = PredictionServiceClient.create(settings)) {

                EndpointName endpointName = EndpointName.ofProjectLocationPublisherModelName(
                        projectId,
                        location,
                        "google",
                        embeddingModel
                );

                Value instance = Value.newBuilder()
                        .setStructValue(
                                Struct.newBuilder()
                                        .putFields("content", Value.newBuilder()
                                                .setStringValue(rawInput)
                                                .build())
                                        .putFields("task_type", Value.newBuilder()
                                                .setStringValue(taskType)
                                                .build())
                                        .build()
                        )
                        .build();

                Value parameters = Value.newBuilder()
                        .setStructValue(
                                Struct.newBuilder()
                                        .putFields("outputDimensionality", Value.newBuilder()
                                                .setNumberValue(embeddingDimension)
                                                .build())
                                        .build()
                        )
                        .build();

                PredictRequest request = PredictRequest.newBuilder()
                        .setEndpoint(endpointName.toString())
                        .addInstances(instance)
                        .setParameters(parameters)
                        .build();

                PredictResponse response = client.predict(request);

                Value prediction = response.getPredictions(0);

                ListValue embeddings = prediction
                        .getStructValue()
                        .getFieldsOrThrow("embeddings")
                        .getStructValue()
                        .getFieldsOrThrow("values")
                        .getListValue();

                List<Float> vector = embeddings.getValuesList()
                        .stream()
                        .map(Value::getNumberValue)
                        .map(Double::floatValue)
                        .collect(Collectors.toList());

                if (vector.size() != embeddingDimension) {
                    throw new AnrealShopException("EMBEDDING_DIMENSION_MISMATCH");
                }

                return vector;
            }

        } catch (AnrealShopException e) {
            throw e;
        } catch (Exception e) {
            throw new AnrealShopException("VERTEX_AI_EMBEDDING_FAILED");
        }
    }

    private static void addContent(List<String> content, String label, Object value) {
        if (value != null && !value.toString().isBlank()) {
            content.add(label + ": " + value);
        }
    }

    public static String toPgVector(List<Float> vector) {
        return "[" + vector.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","))
                + "]";
    }
}
