package com.hanaro.schedule_hanaro.global.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanaro.schedule_hanaro.global.domain.Recommend;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.repository.RecommendRepository;
import com.hanaro.schedule_hanaro.global.utils.TFIDFVectorizer;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class RecommendInitializer implements CommandLineRunner {

    private final RecommendRepository recommendRepository;

    public RecommendInitializer(RecommendRepository recommendRepository) {
        this.recommendRepository = recommendRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 데이터가 이미 존재하는지 확인
        if (recommendRepository.count() == 0) {
            System.out.println("초기 데이터를 저장합니다...");

            // Step 1: JSON 데이터를 로드
            Map<String, Map<String, Object>> tokenizedFAQ = loadTokenizedFAQ("tokenized_faq.json");

            // Step 2: 고유 토큰 추출
            Set<String> allTokens = tokenizedFAQ.values().stream()
                    .flatMap(entry -> {
                        Object tokensObj = entry.get("questionTokens");
                        if (tokensObj instanceof List<?>) {
                            @SuppressWarnings("unchecked")
                            List<String> tokens = (List<String>) tokensObj;
                            return tokens.stream();
                        }
                        return Stream.empty();
                    })
                    .collect(Collectors.toSet());
            List<String> tokenList = new ArrayList<>(allTokens);

            // Step 3: IDF 계산
            Map<String, Double> idfMap = TFIDFVectorizer.calculateIDF(tokenizedFAQ);

            // Step 4: TF-IDF 벡터 생성
            Map<String, RealVector> tfidfVectors = TFIDFVectorizer.createTFIDFVectors(tokenizedFAQ, idfMap, tokenList);

            // Step 5: Recommend 데이터 생성
            List<Recommend> initialData = new ArrayList<>();

            Category[] categories = Category.values();
            Random random = new Random();

            for (Map.Entry<String, Map<String, Object>> entry : tokenizedFAQ.entrySet()) {
                String question = entry.getKey();
                String fullAnswer = (String) entry.getValue().get("fullAnswer");
                RealVector vector = tfidfVectors.get(question);

                Category randomCategory = categories[random.nextInt(categories.length)];

                initialData.add(Recommend.builder()
                        .query(question)
                        .response(fullAnswer)
                        .category(randomCategory)
                        .queryVector(vector.toString())
                        .build());
            }

            // Step 6: 데이터 저장
            recommendRepository.saveAll(initialData);

            System.out.println("초기 데이터 저장이 완료되었습니다.");
        } else {
            System.out.println("데이터가 이미 존재합니다. 저장하지 않습니다.");
        }
    }
    public static Map<String, Map<String, Object>> loadTokenizedFAQ(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(new File(filePath), objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Map.class));
    }
}
