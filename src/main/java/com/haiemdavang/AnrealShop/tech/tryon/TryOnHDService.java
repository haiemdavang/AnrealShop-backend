package com.haiemdavang.AnrealShop.tech.tryon;

import com.haiemdavang.AnrealShop.dto.tryon.TryOnRequest;
import com.haiemdavang.AnrealShop.dto.tryon.TryOnResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TryOnHDService {
    private final RestTemplate restTemplate;
    @Value("${tryonhd.url:http://localhost:8080}")
    private String tryOnHdUrl;

    public TryOnResponse tryOn(TryOnRequest request) {
        try {
            byte[] personImage = Base64.getDecoder().decode(request.getPersonImageBase64());
            byte[] productImage = Base64.getDecoder().decode(request.getProductImageBase64());

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            body.add("file1", new ByteArrayResource(productImage) {
                @Override
                public String getFilename() {
                    return "product.jpg";
                }
            });
            body.add("file2", new ByteArrayResource(personImage) {
                @Override
                public String getFilename() {
                    return "person.jpg";
                }
            });

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            headers.set("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/148.0.0.0 Safari/537.36");

            HttpEntity<MultiValueMap<String, Object>> entity =
                    new HttpEntity<>(body, headers);

            String endpoint = tryOnHdUrl + "/Virtual-Try-On";

            ResponseEntity<Map> response = restTemplate.exchange(
                    endpoint,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                return TryOnResponse.error(
                        "Try-on service returned status: "
                                + response.getStatusCode().value()
                );
            }

            Map<String, Object> responseBody = response.getBody();

            if (responseBody == null)
                return TryOnResponse.error("Empty response from try-on service");

            String resultImage = (String) responseBody.get("image");
            String message = (String) responseBody.get("message");
            if (resultImage == null || resultImage.isBlank())
                return TryOnResponse.error(message != null ? message : "No output image returned");

            return TryOnResponse.success(
                    resultImage,
                    "image/jpeg"
            );

        } catch (Exception ex) {
            log.error("Virtual try-on failed", ex);
            return TryOnResponse.error(
                    "Virtual try-on failed: " + ex.getMessage()
            );
        }
    }
}
