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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                                                .setStringValue("RETRIEVAL_QUERY")
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
                    throw new RuntimeException(
                            "Embedding dimension mismatch. Expected "
                                    + embeddingDimension
                                    + " but got "
                                    + vector.size()
                    );
                }

                return vector;
            }

        } catch (Exception e) {
            throw new RuntimeException("Gọi Vertex AI Embedding thất bại", e);
        }
    }

    public static String toPgVector(List<Float> vector) {
        return "[" + vector.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","))
                + "]";
    }
}