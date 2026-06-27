package com.haiemdavang.AnrealShop.tech.tryon;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageValidationService {

    private final CredentialsProvider credentialsProvider;

    private static final long MAX_SIZE_BYTES = 3 * 1024 * 1024L;

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/avif"
    );

    private static final Map<String, byte[]> MAGIC_NUMBERS = Map.of(
            "image/jpeg", new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},
            "image/png",  new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47},
            "image/webp", new byte[]{0x52, 0x49, 0x46, 0x46},
            "image/avif", new byte[]{0x66, 0x74, 0x79, 0x70}
    );


    public void validateImage(String base64, String field) {
        byte[] imageBytes = decodeBase64(base64, field);
        validateSize(imageBytes, field);
        validateFormat(imageBytes, field);
        validateSafeSearch(imageBytes, field);
    }

    // -----------------------------------------------------------------------
    // Step 1 – Decode
    // -----------------------------------------------------------------------

    private byte[] decodeBase64(String base64, String field) {
        try {
            String clean = stripDataUri(base64);
            return Base64.getDecoder().decode(clean);
        } catch (IllegalArgumentException e) {
            log.warn("[{}] Invalid base64 encoding", field);
            throw new BadRequestException(field.toUpperCase() + "_INVALID_BASE64");
        }
    }

    private String stripDataUri(String base64) {
        // Support both raw base64 and data URIs (data:image/png;base64,...)
        if (base64 != null && base64.contains(",")) {
            return base64.substring(base64.indexOf(',') + 1);
        }
        return base64;
    }

    // -----------------------------------------------------------------------
    // Step 2 – Size
    // -----------------------------------------------------------------------

    private void validateSize(byte[] imageBytes, String field) {
        if (imageBytes.length > MAX_SIZE_BYTES) {
            long sizeMb = imageBytes.length / (1024 * 1024);
            log.warn("[{}] Image too large: {}MB (max 3MB)", field, sizeMb);
            throw new BadRequestException(field.toUpperCase() + "_IMAGE_TOO_LARGE");
        }
    }

    // -----------------------------------------------------------------------
    // Step 3 – Format (magic-number detection)
    // -----------------------------------------------------------------------

    private void validateFormat(byte[] imageBytes, String field) {
        String detectedMime = detectMimeType(imageBytes);

        if (detectedMime == null || !ALLOWED_MIME_TYPES.contains(detectedMime)) {
            log.warn("[{}] Unsupported format: {}", field, detectedMime);
            throw new BadRequestException(field.toUpperCase() + "_UNSUPPORTED_FORMAT");
        }

        log.debug("[{}] Detected format: {}", field, detectedMime);
    }

    private String detectMimeType(byte[] bytes) {
        if (bytes == null || bytes.length < 4) return null;

        // JPEG: FF D8 FF
        if (startsWith(bytes, MAGIC_NUMBERS.get("image/jpeg"))) return "image/jpeg";

        // PNG: 89 50 4E 47
        if (startsWith(bytes, MAGIC_NUMBERS.get("image/png"))) return "image/png";

        // WebP: RIFF????WEBP
        if (startsWith(bytes, MAGIC_NUMBERS.get("image/webp")) && bytes.length >= 12) {
            // Bytes 8–11 must be "WEBP"
            String subtype = new String(bytes, 8, 4);
            if ("WEBP".equals(subtype)) return "image/webp";
        }

        if (bytes[4] == 0x66 && bytes[5] == 0x74 && bytes[6] == 0x79 && bytes[7] == 0x70) {
            // Kiểm tra tiếp xem có phải là 'avif' không (tại offset 8)
            if (bytes[8] == 0x61 && bytes[9] == 0x76 && bytes[10] == 0x69 && bytes[11] == 0x66) {
                return "image/avif";
            }
        }

        return null;
    }

    private boolean startsWith(byte[] data, byte[] signature) {
        if (data.length < signature.length) return false;
        for (int i = 0; i < signature.length; i++) {
            if (data[i] != signature[i]) return false;
        }
        return true;
    }

    // -----------------------------------------------------------------------
    // Step 4 – Safe Search via Google Cloud Vision API
    // -----------------------------------------------------------------------

    private void validateSafeSearch(byte[] imageBytes, String field) {
        log.debug("[{}] Running safe-search detection via Cloud Vision API", field);

        try (ImageAnnotatorClient visionClient = buildVisionClient()) {

            Image image = Image.newBuilder()
                    .setContent(ByteString.copyFrom(imageBytes))
                    .build();

            AnnotateImageRequest annotateRequest = AnnotateImageRequest.newBuilder()
                    .setImage(image)
                    .addFeatures(Feature.newBuilder()
                            .setType(Feature.Type.SAFE_SEARCH_DETECTION)
                            .build())
                    .build();

            BatchAnnotateImagesResponse batchResponse =
                    visionClient.batchAnnotateImages(List.of(annotateRequest));

            AnnotateImageResponse imageResponse = batchResponse.getResponses(0);

            if (imageResponse.hasError()) {
                log.error("[{}] Vision API error: {}", field, imageResponse.getError().getMessage());
                throw new BadRequestException(field.toUpperCase() + "_CONTENT_CHECK_FAILED");
            }

            // 1. LẤY OBJECT SAFESEARCH RA TRƯỚC
            SafeSearchAnnotation safeSearch = imageResponse.getSafeSearchAnnotation();

            // 2. LOG CHI TIẾT CÁC CHỈ SỐ (Dùng log.info để dễ quan sát)
            log.info("[{}] SafeSearch result -> Adult: {}, Violence: {}, Racy: {}, Medical: {}, Spoof: {}",
                    field,
                    safeSearch.getAdult().name(),
                    safeSearch.getViolence().name(),
                    safeSearch.getRacy().name(),
                    safeSearch.getMedical().name(),
                    safeSearch.getSpoof().name());

            // 3. Kiểm tra logic chặn của bạn
            checkSafeSearchLikelihood(safeSearch, field);

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("[{}] Failed to connect to Cloud Vision API: {}", field, e.getMessage(), e);
            throw new BadRequestException(field.toUpperCase() + "_CONTENT_CHECK_FAILED");
        }
    }

    private void checkSafeSearchLikelihood(SafeSearchAnnotation annotation, String field) {

        Likelihood adult = annotation.getAdult();
        Likelihood violence = annotation.getViolence();
        Likelihood racy = annotation.getRacy();

        log.info("[{}] SafeSearch -> Adult={}, Violence={}, Racy={}",
                field,
                adult,
                violence,
                racy);

        // Adult: chặn mạnh
        if (isAdultOrViolenceUnsafe(adult)) {
            log.warn("[{}] Adult content detected: {}", field, adult);
            throw new BadRequestException(field.toUpperCase() + "_CONTAINS_ADULT_CONTENT");
        }

        // Violence: chặn mạnh
        if (isAdultOrViolenceUnsafe(violence)) {
            log.warn("[{}] Violent content detected: {}", field, violence);
            throw new BadRequestException(field.toUpperCase() + "_CONTAINS_VIOLENT_CONTENT");
        }

        // Racy: chỉ chặn VERY_LIKELY
        if (isRacyUnsafe(racy)) {
            log.warn("[{}] Racy content detected: {}", field, racy);
            throw new BadRequestException(field.toUpperCase() + "_CONTAINS_INAPPROPRIATE_CONTENT");
        }
    }

    /**
     * Returns true if likelihood is LIKELY or VERY_LIKELY.
     * POSSIBLE is intentionally allowed to avoid false positives (e.g. swimwear product shots).
     */
    private boolean isAdultOrViolenceUnsafe(Likelihood likelihood) {
        return likelihood == Likelihood.LIKELY
                || likelihood == Likelihood.VERY_LIKELY;
    }

    private boolean isRacyUnsafe(Likelihood likelihood) {
        return likelihood == Likelihood.VERY_LIKELY;
    }

    // -----------------------------------------------------------------------
    // Vision client factory
    // -----------------------------------------------------------------------

    private ImageAnnotatorClient buildVisionClient() throws IOException {
        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(credentialsProvider)
                .build();
        return ImageAnnotatorClient.create(settings);
    }
}