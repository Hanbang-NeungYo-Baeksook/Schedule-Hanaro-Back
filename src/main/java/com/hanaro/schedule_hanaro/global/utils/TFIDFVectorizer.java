package com.hanaro.schedule_hanaro.global.utils;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.*;
import java.util.stream.Collectors;

public class TFIDFVectorizer {

    // TF (단어 빈도) 계산
    private static Map<String, Double> calculateTF(List<String> tokens) {
        Map<String, Double> tfMap = new HashMap<>();
        int totalTokens = tokens.size();

        // 각 토큰의 빈도 계산
        tokens.forEach(token ->
                tfMap.put(token, tfMap.getOrDefault(token, 0.0) + 1.0 / totalTokens)
        );

        return tfMap;
    }

    // IDF (역문서 빈도) 계산
    public static Map<String, Double> calculateIDF(Map<String, Map<String, Object>> tokenizedFAQ) {
        int totalDocuments = tokenizedFAQ.size();
        Map<String, Integer> documentFrequency = new HashMap<>();

        // 각 토큰이 등장하는 문서 수 계산
        tokenizedFAQ.values().forEach(entry -> {
            @SuppressWarnings("unchecked")
            Set<String> uniqueTokensInDoc = new HashSet<>((List<String>) entry.get("questionTokens"));
            uniqueTokensInDoc.forEach(token ->
                    documentFrequency.put(token, documentFrequency.getOrDefault(token, 0) + 1)
            );
        });

        // IDF 계산: log(총 문서 수 / 해당 토큰이 등장하는 문서 수)
        return documentFrequency.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> Math.log((double) totalDocuments / (entry.getValue() + 1.0))
                ));
    }

    // TF-IDF 벡터 생성
    public static Map<String, RealVector> createTFIDFVectors(
            Map<String, Map<String, Object>> tokenizedFAQ,
            Map<String, Double> idfMap,
            List<String> allTokens
    ) {
        Map<String, RealVector> tfidfVectors = new HashMap<>();

        // 각 FAQ 질문에 대해 TF-IDF 벡터 생성
        for (Map.Entry<String, Map<String, Object>> entry : tokenizedFAQ.entrySet()) {
            @SuppressWarnings("unchecked")
            List<String> tokens = (List<String>) entry.getValue().get("questionTokens");

            // TF 계산
            Map<String, Double> tfMap = calculateTF(tokens);

            // 벡터 초기화
            double[] tfidfVector = new double[allTokens.size()];

            // TF-IDF 계산
            for (int i = 0; i < allTokens.size(); i++) {
                String token = allTokens.get(i);
                if (tfMap.containsKey(token)) {
                    double idf = idfMap.getOrDefault(token, 0.0);
                    tfidfVector[i] = tfMap.get(token) * idf;
                }
            }

            tfidfVectors.put(entry.getKey(), new ArrayRealVector(tfidfVector));
        }

        return tfidfVectors;
    }

    // 새로운 질문에 대한 TF-IDF 벡터 생성
    public static RealVector createTFIDFVectorForNewQuestion(
            String newQuestion,
            Map<String, Double> idfMap,
            List<String> allTokens
    ) {
        // 새 질문 토큰화
        List<String> newQuestionTokens = FAQTokenizer.tokenizeNewQuestion(newQuestion);

        // TF 계산
        Map<String, Double> tfMap = calculateTF(newQuestionTokens);

        // 벡터 초기화
        double[] tfidfVector = new double[allTokens.size()];

        // TF-IDF 계산
        for (int i = 0; i < allTokens.size(); i++) {
            String token = allTokens.get(i);
            if (tfMap.containsKey(token)) {
                double idf = idfMap.getOrDefault(token, 0.0);
                tfidfVector[i] = tfMap.get(token) * idf;
            }
        }

        return new ArrayRealVector(tfidfVector);
    }

    // 코사인 유사도 계산
    public static double calculateCosineSimilarity(RealVector vector1, RealVector vector2) {
        double dotProduct = vector1.dotProduct(vector2);
        double normalization = vector1.getNorm() * vector2.getNorm();

        // 0으로 나누는 것을 방지
        if (normalization == 0) {
            return 0.0;
        }

        return dotProduct / normalization;
    }

}
