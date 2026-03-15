package com.haiemdavang.AnrealShop.controller;

import com.haiemdavang.AnrealShop.dto.kyc.ScanIdRequest;
import com.haiemdavang.AnrealShop.dto.kyc.ScanIdResponse;
import com.haiemdavang.AnrealShop.dto.kyc.VerifyFaceRequest;
import com.haiemdavang.AnrealShop.dto.kyc.VerifyFaceResponse;
import com.haiemdavang.AnrealShop.tech.kyc.KycService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kyc")
@Tag(name = "KYC")
public class KycController {

    private final KycService kycService;

    @PostMapping("/scan-id")
    public ResponseEntity<ScanIdResponse> scanId(@Valid @RequestBody ScanIdRequest request) {
        ScanIdResponse response = kycService.scanId(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-face")
    public ResponseEntity<VerifyFaceResponse> verifyFace(@Valid @RequestBody VerifyFaceRequest request) {
        VerifyFaceResponse response = kycService.verifyFace(request);
        return ResponseEntity.ok(response);
    }
}
