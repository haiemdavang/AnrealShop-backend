package com.haiemdavang.AnrealShop.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haiemdavang.AnrealShop.dto.common.ErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtEntryPoint implements AuthenticationEntryPoint {
    private final Environment env;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String errorMessage = (String) request.getAttribute("exceptionMessage");
        if (errorMessage == null) {
            errorMessage = authException.getMessage();
        }else{
            errorMessage = env.getProperty(errorMessage, "Vui long dang nhap lai");
        }

        ErrorResponseDto responseDto = ErrorResponseDto.builder()
                .code(Integer.toString(HttpStatus.UNAUTHORIZED.value()))
                .message(errorMessage)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), responseDto);
    }
}
