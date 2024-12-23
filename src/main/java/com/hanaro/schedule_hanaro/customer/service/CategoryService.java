package com.hanaro.schedule_hanaro.customer.service;

import com.hanaro.schedule_hanaro.customer.dto.response.CategoryRecommendResponse;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.utils.CategoryRecommender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    public CategoryRecommendResponse getRecommendCategory(String content) {
        // CategoryRecommender를 통해 카테고리와 유사도 점수를 얻음
        Map.Entry<Category, Double> result = CategoryRecommender.recommendCategory(content);
        
        // confidence 값을 소수점 2자리까지만 표시
        double roundedConfidence = Math.round(result.getValue() * 100) / 100.0;
        
        return CategoryRecommendResponse.from(
            result.getKey(),
            roundedConfidence
        );
    }
} 