package com.hanaro.schedule_hanaro.customer.controller;

import com.hanaro.schedule_hanaro.customer.dto.request.CategoryRecommendRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.CategoryRecommendResponse;
import com.hanaro.schedule_hanaro.customer.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Category", description = "카테고리 추천 API")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "카테고리 추천", description = "질문 내용을 기반으로 적절한 카테고리를 추천합니다.")
    @PostMapping("/recommend")
    public ResponseEntity<CategoryRecommendResponse> recommendCategory(
            @RequestBody CategoryRecommendRequest request) {
        return ResponseEntity.ok(categoryService.getRecommendCategory(request.content()));
    }
} 