package com.haiemdavang.AnrealShop.controller;

import com.haiemdavang.AnrealShop.dto.attribute.AttributeResponse;
import com.haiemdavang.AnrealShop.service.serviceInter.IAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/attributes")
public class AttributeController {
    
    private final IAttributeService attributeService;
    
    @GetMapping("/my-shop")
    public ResponseEntity<AttributeResponse> getAllAttributes() {
        return ResponseEntity.ok(attributeService.getAttributesForShop());
    }
}
