package com.haiemdavang.AnrealShop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/healthy")
public class HealthyController {
    @GetMapping
    public ResponseEntity<?> getCart() {
        return ResponseEntity.ok(Map.of(
                "message", "Healthy check",
                "status", "OK"
        ));
    }

    @GetMapping("/sentry-test")
    public String test() {
        throw new RuntimeException("This error test");
    }

}
