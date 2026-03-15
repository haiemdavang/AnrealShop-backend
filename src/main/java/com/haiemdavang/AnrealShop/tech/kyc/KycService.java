package com.haiemdavang.AnrealShop.tech.kyc;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.haiemdavang.AnrealShop.dto.kyc.ScanIdRequest;
import com.haiemdavang.AnrealShop.dto.kyc.ScanIdResponse;
import com.haiemdavang.AnrealShop.dto.kyc.VerifyFaceRequest;
import com.haiemdavang.AnrealShop.dto.kyc.VerifyFaceResponse;
import com.haiemdavang.AnrealShop.exception.AnrealShopException;
import com.haiemdavang.AnrealShop.exception.BadRequestException;
import com.haiemdavang.AnrealShop.modal.enums.DocumentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class KycService {

    private final CredentialsProvider credentialsProvider;

    /**
     * Tạo ImageAnnotatorClient với credentials từ spring-cloud-gcp (thống nhất với TryOnService)
     */
    private ImageAnnotatorClient createVisionClient() throws IOException {
        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(credentialsProvider)
                .build();
        return ImageAnnotatorClient.create(settings);
    }

    public ScanIdResponse scanId(ScanIdRequest request) {
        String rawText = extractTextFromImage(request.getImageBase64());
        log.info("OCR raw text: {}", rawText);
        return parseIdCard(rawText);
    }

    private String extractTextFromImage(String imageBase64) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
            ByteString imgByteString = ByteString.copyFrom(imageBytes);

            Image image = Image.newBuilder().setContent(imgByteString).build();
            Feature feature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest visionRequest = AnnotateImageRequest.newBuilder()
                    .addFeatures(feature)
                    .setImage(image)
                    .build();

            try (ImageAnnotatorClient client = createVisionClient()) {
                BatchAnnotateImagesResponse response = client.batchAnnotateImages(List.of(visionRequest));
                AnnotateImageResponse imageResponse = response.getResponses(0);

                if (imageResponse.hasError()) {
                    throw new AnrealShopException("Google Vision error: " + imageResponse.getError().getMessage());
                }

                if (imageResponse.getTextAnnotationsList().isEmpty()) {
                    throw new BadRequestException("CANNOT_DETECT_TEXT_FROM_IMAGE");
                }
                return imageResponse.getFullTextAnnotation().getText();
            }
        } catch (AnrealShopException e) {
            throw e;
        } catch (IOException e) {
            log.error("Error connecting to Google Vision OCR", e);
            throw new AnrealShopException("OCR_FAILED: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error calling Google Vision OCR", e);
            throw new AnrealShopException("OCR_FAILED: " + e.getMessage());
        }
    }

    private ScanIdResponse parseIdCard(String rawText) {
        // Giữ nguyên rawText có newline để regex tách đúng từng field
        // Chỉ normalize cho việc detect loại giấy tờ
        String upper = rawText.toUpperCase();

        DocumentType documentType = DocumentType.CCCD;
        if (upper.contains("PASSPORT") || upper.contains("HỘ CHIẾU")) {
            documentType = DocumentType.HO_CHIEU;
        }

        String documentNumber = extractDocumentNumber(rawText, documentType);
        String fullName = extractFullName(rawText);
        String dateOfBirth = extractDateOfBirth(rawText);
        String gender = extractGender(rawText);
        String nationality = extractNationality(rawText);
        String placeOfOrigin = extractPlaceOfOrigin(rawText);
        String placeOfResidence = extractPlaceOfResidence(rawText);
        String expiryDate = extractExpiryDate(rawText);

        return ScanIdResponse.builder()
                .fullName(fullName)
                .documentNumber(documentNumber)
                .documentType(documentType)
                .dateOfBirth(dateOfBirth)
                .gender(gender)
                .nationality(nationality)
                .placeOfOrigin(placeOfOrigin)
                .placeOfResidence(placeOfResidence)
                .expiryDate(expiryDate)
                .rawText(rawText)
                .build();
    }

    /**
     * Chuẩn hoá chuỗi nhiều dòng thành 1 dòng, xoá ký tự rác (回, |, I đứng riêng)
     */
    private String collapseLines(String text) {
        return text.replace("\n", " ")
                .replaceAll("[回|]", " ")
                .replaceAll("\\bI\\b", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String extractDocumentNumber(String text, DocumentType type) {
        if (type == DocumentType.CCCD) {
            // Số CCCD: 12 chữ số, thường đứng sau "No" hoặc "Số"
            Pattern pattern = Pattern.compile("(?:No[.:]?|Số[.:]?)\\s*(\\d{12})", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group(1);
            }
            // Fallback: tìm chuỗi 12 chữ số bất kỳ
            Pattern fallback = Pattern.compile("\\b(\\d{12})\\b");
            Matcher fallbackMatcher = fallback.matcher(text);
            if (fallbackMatcher.find()) {
                return fallbackMatcher.group(1);
            }
        } else {
            // Hộ chiếu: pattern chữ + số
            Pattern pattern = Pattern.compile("\\b([A-Z]\\d{7,8})\\b");
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private String extractFullName(String text) {
        // Tìm label rồi lấy nội dung trên cùng dòng hoặc dòng kế tiếp
        Pattern pattern = Pattern.compile(
                "(?:Họ\\s*(?:và|va)\\s*tên|Full\\s*name)[^\\S\\n]*[:/]?[^\\S\\n]*\\n?\\s*([A-ZÀ-Ỹ][A-ZÀ-Ỹa-zà-ỹ\\s]+?)\\s*(?:\\n|Ngày|Date|Giới|Sex)",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        );
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private String extractDateOfBirth(String text) {
        // Ưu tiên: tìm ngày nằm ngay sau "Date of birth" / "Ngày sinh" (cùng dòng)
        Pattern direct = Pattern.compile(
                "(?:Ngày[,]?\\s*(?:tháng[,]?\\s*năm)?\\s*sinh|Date\\s*of\\s*birth)[^\\d]*(\\d{2}[/.-]\\d{2}[/.-]\\d{4})",
                Pattern.CASE_INSENSITIVE
        );
        Matcher directMatcher = direct.matcher(text);
        if (directMatcher.find()) {
            return directMatcher.group(1).replace(".", "/").replace("-", "/");
        }

        // Nếu không tìm được trực tiếp → thu thập tất cả ngày, loại trừ expiry date
        String expiryDate = extractExpiryDate(text);
        Pattern allDates = Pattern.compile("(\\d{2}[/.-]\\d{2}[/.-]\\d{4})");
        Matcher allMatcher = allDates.matcher(text);
        while (allMatcher.find()) {
            String date = allMatcher.group(1).replace(".", "/").replace("-", "/");
            // Bỏ qua ngày trùng với expiry
            if (!date.equals(expiryDate)) {
                return date;
            }
        }
        return null;
    }

    private String extractGender(String text) {
        Pattern pattern = Pattern.compile(
                "(?:Giới\\s*tính|Sex)[^\\S\\n]*/?,?[^\\S\\n]*[:/]?\\s*(Nam|Nữ|Male|Female)",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        );
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private String extractNationality(String text) {
        // Lấy nội dung sau "Nationality" / "Quốc tịch" đến hết dòng
        Pattern pattern = Pattern.compile(
                "(?:Quốc\\s*tịch|Nationality)[^\\S\\n]*[:/]?\\s*([A-ZÀ-Ỹa-zà-ỹ\\s]+?)\\s*(?:\\n|$)",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE
        );
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private String extractPlaceOfOrigin(String text) {
        // Chiến lược 1: Lấy text giữa dòng Nationality và dòng "Nơi thường trú"/"Place of residence"
        Pattern betweenNatAndRes = Pattern.compile(
                "(?:Quốc\\s*tịch|Nationality)[^\\n]*\\n(.+?)\\n\\s*(?:Nơi\\s*thường\\s*trú|Place\\s*of\\s*residence)",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL
        );
        Matcher m1 = betweenNatAndRes.matcher(text);
        if (m1.find()) {
            String raw = m1.group(1);
            return collapseLines(raw)
                    .replaceAll("\\d{2}[/.-]\\d{2}[/.-]\\d{4}", "")  // xoá date lẫn vào
                    .replaceAll("[回]", "")
                    .replaceAll("\\s+", " ")
                    .trim();
        }

        // Chiến lược 2: fallback — lấy từ "Place of origin" đến "Nơi thường trú"
        Pattern fallback = Pattern.compile(
                "(?:Quê\\s*quán|Place\\s*of\\s*origin)[^\\S\\n]*[:/]?\\s*(.+?)\\s*(?:Nơi\\s*thường\\s*trú|Place\\s*of\\s*residence)",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL
        );
        Matcher m2 = fallback.matcher(text);
        if (m2.find()) {
            String raw = m2.group(1);
            return collapseLines(raw)
                    .replaceAll("\\d{2}[/.-]\\d{2}[/.-]\\d{4}", "")
                    .replaceAll("(?i)Quốc\\s*tịch.*?(?:Việt\\s*Nam|Vietnamese)", "")
                    .replaceAll("(?i)Nationality.*?(?:Việt\\s*Nam|Vietnamese)", "")
                    .replaceAll("[回]", "")
                    .replaceAll("\\s+", " ")
                    .trim();
        }
        return null;
    }

    private String extractPlaceOfResidence(String text) {
        Pattern pattern = Pattern.compile(
                "(?:Nơi\\s*thường\\s*trú|Place\\s*of\\s*residence)[^\\S\\n]*[:/]?\\s*(.+?)\\s*(?:Có\\s*giá\\s*trị|Date\\s*of\\s*expiry|Expires?|$)",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.DOTALL
        );
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return collapseLines(matcher.group(1));
        }
        return null;
    }

    private String extractExpiryDate(String text) {
        Pattern pattern = Pattern.compile(
                "(?:Có\\s*giá\\s*trị\\s*đến|Date\\s*of\\s*expiry|Expires?)[^\\d]*(\\d{2}[/.-]\\d{2}[/.-]\\d{4})",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).replace(".", "/").replace("-", "/");
        }
        return null;
    }

    public VerifyFaceResponse verifyFace(VerifyFaceRequest request) {
        List<FaceAnnotation> idFaces = detectFaces(request.getIdImageBase64());
        List<FaceAnnotation> selfieFaces = detectFaces(request.getSelfieImageBase64());

        if (idFaces.isEmpty()) {
            return VerifyFaceResponse.builder()
                    .matched(false)
                    .confidence(0)
                    .message("Không phát hiện khuôn mặt trên ảnh giấy tờ")
                    .idFaceCount(0)
                    .selfieFaceCount(selfieFaces.size())
                    .build();
        }

        if (selfieFaces.isEmpty()) {
            return VerifyFaceResponse.builder()
                    .matched(false)
                    .confidence(0)
                    .message("Không phát hiện khuôn mặt trên ảnh chân dung")
                    .idFaceCount(idFaces.size())
                    .selfieFaceCount(0)
                    .build();
        }

        if (selfieFaces.size() > 1) {
            return VerifyFaceResponse.builder()
                    .matched(false)
                    .confidence(0)
                    .message("Ảnh chân dung chứa nhiều hơn 1 khuôn mặt")
                    .idFaceCount(idFaces.size())
                    .selfieFaceCount(selfieFaces.size())
                    .build();
        }

        FaceAnnotation idFace = idFaces.get(0);
        FaceAnnotation selfieFace = selfieFaces.get(0);

        double confidence = compareFaceAnnotations(idFace, selfieFace);
        boolean matched = confidence >= 0.6;

        String message = matched
                ? "Xác thực khuôn mặt thành công"
                : "Khuôn mặt không khớp hoặc độ tin cậy thấp";

        return VerifyFaceResponse.builder()
                .matched(matched)
                .confidence(Math.round(confidence * 100.0) / 100.0)
                .message(message)
                .idFaceCount(idFaces.size())
                .selfieFaceCount(selfieFaces.size())
                .build();
    }

    private List<FaceAnnotation> detectFaces(String imageBase64) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
            ByteString imgByteString = ByteString.copyFrom(imageBytes);

            Image image = Image.newBuilder().setContent(imgByteString).build();
            Feature feature = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();
            AnnotateImageRequest visionRequest = AnnotateImageRequest.newBuilder()
                    .addFeatures(feature)
                    .setImage(image)
                    .build();

            try (ImageAnnotatorClient client = createVisionClient()) {
                BatchAnnotateImagesResponse response = client.batchAnnotateImages(List.of(visionRequest));
                AnnotateImageResponse imageResponse = response.getResponses(0);

                if (imageResponse.hasError()) {
                    throw new AnrealShopException("Google Vision error: " + imageResponse.getError().getMessage());
                }

                return imageResponse.getFaceAnnotationsList();
            }
        } catch (AnrealShopException e) {
            throw e;
        } catch (IOException e) {
            log.error("Error connecting to Google Vision Face Detection", e);
            throw new AnrealShopException("FACE_DETECTION_FAILED: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error calling Google Vision Face Detection", e);
            throw new AnrealShopException("FACE_DETECTION_FAILED: " + e.getMessage());
        }
    }

    private double compareFaceAnnotations(FaceAnnotation idFace, FaceAnnotation selfieFace) {
        double score = 0.0;
        int factors = 0;

        // 1. Detection confidence của cả 2 ảnh
        double idConfidence = idFace.getDetectionConfidence();
        double selfieConfidence = selfieFace.getDetectionConfidence();
        double avgConfidence = (idConfidence + selfieConfidence) / 2.0;
        score += avgConfidence;
        factors++;

        // 2. So sánh landmarking confidence
        double idLandmarkConfidence = idFace.getLandmarkingConfidence();
        double selfieLandmarkConfidence = selfieFace.getLandmarkingConfidence();
        double avgLandmark = (idLandmarkConfidence + selfieLandmarkConfidence) / 2.0;
        score += avgLandmark;
        factors++;

        // 3. Kiểm tra góc xoay mặt không quá khác biệt
        double panDiff = Math.abs(idFace.getPanAngle() - selfieFace.getPanAngle());
        double tiltDiff = Math.abs(idFace.getTiltAngle() - selfieFace.getTiltAngle());
        double rollDiff = Math.abs(idFace.getRollAngle() - selfieFace.getRollAngle());

        // Góc xoay không quá 30 độ khác biệt -> tốt
        double angleScore = 1.0 - Math.min(1.0, (panDiff + tiltDiff + rollDiff) / 90.0);
        score += angleScore;
        factors++;

        // 4. Kiểm tra cả 2 ảnh đều detect rõ face (confidence > 0.7)
        if (idConfidence > 0.7 && selfieConfidence > 0.7) {
            score += 0.8;
            factors++;
        }

        return factors > 0 ? score / factors : 0;
    }
}
