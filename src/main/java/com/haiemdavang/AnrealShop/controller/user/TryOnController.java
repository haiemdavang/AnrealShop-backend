package com.haiemdavang.AnrealShop.controller.user;

import com.haiemdavang.AnrealShop.dto.tryon.TryOnRequest;
import com.haiemdavang.AnrealShop.dto.tryon.TryOnResponse;
import com.haiemdavang.AnrealShop.tech.tryon.TryOnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tryon")
@Tag(name = "Virtual Try-On", description = "Virtual Try-On API using Google Cloud AI Platform")
public class TryOnController {

    private final TryOnService tryOnService;

    @PostMapping("/detect")
    @Operation(summary = "Virtual Try-On", description = "Try on a product image on a person image using AI")
    public ResponseEntity<TryOnResponse> tryOnDetect(@Valid @RequestBody TryOnRequest request) {
        TryOnResponse response = tryOnService.tryOn(request);
        return ResponseEntity.ok(response);
    }
}
