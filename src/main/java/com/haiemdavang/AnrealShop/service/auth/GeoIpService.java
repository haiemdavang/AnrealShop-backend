package com.haiemdavang.AnrealShop.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class GeoIpService {

    public String getLocationFromIp(String ip) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://ipwho.is/" + ip;

            Map result = restTemplate.getForObject(url, Map.class);

            assert result != null;
            return result.get("city") + ", " + result.get("country");
        } catch (Exception e) {
            log.warn("Failed to get location for IP: {}", ip, e);
            return "Unknown";
        }
    }
}
