package com.haiemdavang.AnrealShop.controller;

import com.haiemdavang.AnrealShop.dto.search.publicSearchResponse;
import com.haiemdavang.AnrealShop.tech.elasticsearch.service.PublicSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/search")
@RequiredArgsConstructor
public class PublicSearchController {
    private final PublicSearchService publicSearchService;

    @GetMapping("/suggest")
    public ResponseEntity<publicSearchResponse> suggestSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "5") int productLimit,
            @RequestParam(defaultValue = "3") int categoryLimit) {

        publicSearchResponse response = publicSearchService.suggestSearch(keyword, productLimit, categoryLimit);
        return ResponseEntity.ok(response);
    }
}
