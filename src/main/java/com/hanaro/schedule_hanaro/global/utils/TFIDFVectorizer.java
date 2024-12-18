package com.hanaro.schedule_hanaro.global.utils;

import com.hanaro.schedule_hanaro.global.domain.Recommend;
import lombok.Getter;
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
            Set<String> uniqueTokensInDoc = new HashSet<>((List<String>)entry.get("questionTokens"));
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

        // 토큰 인덱스 매핑
        Map<String, Integer> tokenIndexMap = new HashMap<>();
        for (int i = 0; i < allTokens.size(); i++) {
            tokenIndexMap.put(allTokens.get(i), i);
        }

        // 각 FAQ 질문에 대해 TF-IDF 벡터 생성
        for (Map.Entry<String, Map<String, Object>> entry : tokenizedFAQ.entrySet()) {
            @SuppressWarnings("unchecked")
            List<String> tokens = (List<String>) entry.getValue().get("questionTokens");

            // TF 계산
            Map<String, Double> tfMap = calculateTF(tokens);

            // 벡터 초기화
            double[] tfidfVector = new double[allTokens.size()];

            // TF-IDF 계산
            for (Map.Entry<String, Double> tfEntry : tfMap.entrySet()) {
                String token = tfEntry.getKey();
                if (tokenIndexMap.containsKey(token)) {
                    int index = tokenIndexMap.get(token);
                    double idf = idfMap.getOrDefault(token, 0.0);
                    tfidfVector[index] = tfEntry.getValue() * idf;
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

        // 토큰 인덱스 매핑
        Map<String, Integer> tokenIndexMap = new HashMap<>();
        for (int i = 0; i < allTokens.size(); i++) {
            tokenIndexMap.put(allTokens.get(i), i);
        }

        // TF 계산
        Map<String, Double> tfMap = calculateTF(newQuestionTokens);

        // 벡터 초기화
        double[] tfidfVector = new double[allTokens.size()];

        // TF-IDF 계산
        for (Map.Entry<String, Double> tfEntry : tfMap.entrySet()) {
            String token = tfEntry.getKey();
            if (tokenIndexMap.containsKey(token)) {
                int index = tokenIndexMap.get(token);
                double idf = idfMap.getOrDefault(token, 0.0);
                tfidfVector[index] = tfEntry.getValue() * idf;
            }
        }

        return new ArrayRealVector(tfidfVector);
    }

    // 하이브리드 유사도 계산 (TF-IDF + 코사인 유사도)
    public static List<TFIDFVectorizer.QuestionSimilarity> getTop3SimilarQuestionsTFIDF(
            Map<String, Map<String, Object>> tokenizedFAQ,
            Map<String, RealVector> tfidfVectors,
            RealVector newQuestionTFIDFVector,
            double cosineSimilarityWeight,
            double tfidfSimilarityWeight
    ) {
        Map<String, Double> similarityScores = new HashMap<>();

        for (String question : tokenizedFAQ.keySet()) {
            RealVector questionVector = tfidfVectors.get(question);

            // 코사인 유사도
            double cosineSimilarity = TFIDFVectorizer.calculateCosineSimilarity(
                    newQuestionTFIDFVector,
                    questionVector
            );

            // TF-IDF 벡터의 유클리드 거리 기반 유사도 (거리가 가까울수록 유사도 높음)
            double tfidfSimilarity = 1.0 / (1.0 + newQuestionTFIDFVector.subtract(questionVector).getNorm());

            // 가중치를 적용한 하이브리드 유사도
            double hybridSimilarity = (cosineSimilarityWeight * cosineSimilarity) +
                    (tfidfSimilarityWeight * tfidfSimilarity);

            similarityScores.put(question, hybridSimilarity);
        }

        // 상위 유사 질문 3개 선택 및 반환
        return similarityScores.entrySet().stream()
                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                .limit(3)
                .map(entry -> {
                    String question = entry.getKey();
                    double similarity = entry.getValue();
                    String answer = (String) tokenizedFAQ.get(question).get("fullAnswer");
                    return new TFIDFVectorizer.QuestionSimilarity(question, similarity, answer);
                })
                .collect(Collectors.toList());
    }

    public static String vectorToString(RealVector vector) {
        return Arrays.stream(vector.toArray())
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));
    }

    // String -> RealVector (역직렬화)
    public static RealVector stringToVector(String vectorString) {
        double[] vectorData = Arrays.stream(vectorString.split(","))
                .mapToDouble(Double::parseDouble)
                .toArray();
        return new ArrayRealVector(vectorData);
    }

    @Getter
    public static class QuestionSimilarity {
        private String question;
        private double similarity;
        private String answer;

        public QuestionSimilarity(String question, double similarity, String answer) {
            this.question = question;
            this.similarity = similarity;
            this.answer = answer;
        }

    }

    public static double calculateCosineSimilarity(RealVector vector1, RealVector vector2) {
        double dotProduct = vector1.dotProduct(vector2);
        double normalization = vector1.getNorm() * vector2.getNorm();

        // 0으로 나누는 것을 방지
        if (normalization == 0) {
            return 0.0;
        }

        return dotProduct / normalization;
    }

    public static Map<String, RealVector> createTFIDFVectors(List<Recommend> recommends) {
        Map<String, RealVector> tfidfVectors = new HashMap<>();

        for (Recommend recommend : recommends) {
            String query = recommend.getQuery();
            String queryVectorString = recommend.getQueryVector();

            // queryVector를 RealVector로 변환
            RealVector queryVector = stringToVector(queryVectorString);

            // Map에 추가
            tfidfVectors.put(query, queryVector);
        }

        return tfidfVectors;
    }
}
