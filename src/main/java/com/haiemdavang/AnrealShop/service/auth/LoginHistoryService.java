package com.haiemdavang.AnrealShop.service.auth;

import com.haiemdavang.AnrealShop.dto.auth.HistoryLoginDto;
import com.haiemdavang.AnrealShop.mapper.HistoryLoginMapper;
import com.haiemdavang.AnrealShop.modal.entity.user.HistoryLogin;
import com.haiemdavang.AnrealShop.modal.entity.user.User;
import com.haiemdavang.AnrealShop.repository.HistoryLoginRepository;
import com.haiemdavang.AnrealShop.security.SecurityUtils;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class LoginHistoryService {
    private final GeoIpService geoIpService;
    private final SecurityUtils securityUtil;
    private final HistoryLoginRepository historyRepo;
    private final HistoryLoginMapper historyLoginMapper;

    public void saveLoginHistory(HttpServletRequest request) {

        User user = securityUtil.getCurrentUser();
        String ip = getClientIP(request);
        String userAgent = request.getHeader("User-Agent");
        String device = parseDevice(userAgent);
        String location = geoIpService.getLocationFromIp(ip);

        HistoryLogin history = HistoryLogin.builder()
                .user(user)
                .ipAddress(ip)
                .userAgent(userAgent)
                .device(device)
                .location(location)
                .loginAt(LocalDateTime.now())
                .build();

        HistoryLogin existingDevices = historyRepo.findByUserAndDevice(user, device);

        if (existingDevices != null) {
            history.setId(existingDevices.getId());
        }

        historyRepo.save(history);
    }

    private String getClientIP(HttpServletRequest request) {
        String header = request.getHeader("X-Forwarded-For");
        if (header == null) {
            return request.getRemoteAddr();
        }
        return header.split(",")[0];
    }

    private String parseDevice(String userAgentString) {
        if (userAgentString == null || userAgentString.isEmpty()) {
            return "Unknown Device";
        }
        try {
            UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
            return userAgent.getOperatingSystem().getName();

        } catch (Exception e) {
            return "Unknown Device";
        }
    }

    public List<HistoryLoginDto> getLoginHistory(HttpServletRequest request) {
        User user = securityUtil.getCurrentUser();
        List<HistoryLogin> historyLogins = historyRepo.findByUserOrderByLoginAtDesc(user);
        return historyLogins.stream()
                .map(history -> {
                    HistoryLoginDto dto = historyLoginMapper.toHistoryLoginDto(history);
                    String currentDevice = parseDevice(request.getHeader("User-Agent"));
                    dto.setCurrentSession(currentDevice.equals(history.getDevice()) && history.getLogoutAt() == null);
                    return dto;
                })
                .toList();
    }
}
