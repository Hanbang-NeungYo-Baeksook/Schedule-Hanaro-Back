package com.hanaro.schedule_hanaro.global.utils;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;

import java.util.*;
import java.util.stream.Collectors;

public class TFIDFVectorizer {

    // TF (단어 빈도) 계산
    private static Map<String, Double> calculateTF(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            return Collections.emptyMap(); // 빈 리스트일 경우 빈 맵 반환
        }

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
        validateTokenizedFAQ(tokenizedFAQ);

        int totalDocuments = tokenizedFAQ.size();
        Map<String, Integer> documentFrequency = new HashMap<>();

        // 각 토큰이 등장하는 문서 수 계산
        tokenizedFAQ.values().forEach(entry -> {
            validateFAQEntry(entry);
            List<String> tokens = extractTokens(entry);
            updateDocumentFrequency(documentFrequency, tokens);
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
        validateCreateTFIDFParameters(tokenizedFAQ, idfMap, allTokens);

        Map<String, RealVector> tfidfVectors = new HashMap<>();

        // 각 FAQ 질문에 대해 TF-IDF 벡터 생성
        for (Map.Entry<String, Map<String, Object>> entry : tokenizedFAQ.entrySet()) {
            validateFAQEntry(entry.getValue());
            List<String> tokens = extractTokens(entry.getValue());

            // TF 계산
            Map<String, Double> tfMap = calculateTF(tokens);
            double[] tfidfVector = calculateTFIDFVector(tfMap, idfMap, allTokens);

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
        if (newQuestion == null || newQuestion.isEmpty()) {
            throw new GlobalException(ErrorCode.MISSING_REQUEST_PARAMETER, "New question is null or empty.");
        }
        validateIDFAndTokenParameters(idfMap, allTokens);

        // 새 질문 토큰화
        List<String> newQuestionTokens = FAQTokenizer.tokenizeNewQuestion(newQuestion);
        Map<String, Double> tfMap = calculateTF(newQuestionTokens);

        // TF-IDF 벡터 계산
        double[] tfidfVector = calculateTFIDFVector(tfMap, idfMap, allTokens);

        return new ArrayRealVector(tfidfVector);
    }

    // 코사인 유사도 계산
    public static double calculateCosineSimilarity(RealVector vector1, RealVector vector2) {
        if (vector1 == null || vector2 == null) {
            throw new GlobalException(ErrorCode.MISSING_REQUEST_PARAMETER, "Input vectors must not be null.");
        }
        double dotProduct = vector1.dotProduct(vector2);
        double normalization = vector1.getNorm() * vector2.getNorm();

        // 0으로 나누는 것을 방지
        return normalization == 0 ? 0.0 : dotProduct / normalization;
    }

    // 유효성 검사 메서드들
    private static void validateTokenizedFAQ(Map<String, Map<String, Object>> tokenizedFAQ) {
        if (tokenizedFAQ == null || tokenizedFAQ.isEmpty()) {
            throw new GlobalException(ErrorCode.WRONG_REQUEST_PARAMETER, "Tokenized FAQ data is empty or null.");
        }
    }

    private static void validateFAQEntry(Map<String, Object> entry) {
        if (entry == null) {
            throw new GlobalException(ErrorCode.WRONG_REQUEST_PARAMETER, "Invalid FAQ entry format. Expected Map<String, Object>.");
        }
    }

    private static List<String> extractTokens(Map<String, Object> entry) {
        @SuppressWarnings("unchecked")
        List<String> tokens = (List<String>) entry.get("questionTokens");
        if (tokens == null) {
            throw new GlobalException(ErrorCode.WRONG_REQUEST_PARAMETER, "Tokens are null in FAQ entry.");
        }
        return tokens;
    }

    private static void updateDocumentFrequency(Map<String, Integer> documentFrequency, List<String> tokens) {
        Set<String> uniqueTokensInDoc = new HashSet<>(tokens);
        uniqueTokensInDoc.forEach(token ->
                documentFrequency.put(token, documentFrequency.getOrDefault(token, 0) + 1)
        );
    }

    private static double[] calculateTFIDFVector(Map<String, Double> tfMap, Map<String, Double> idfMap, List<String> allTokens) {
        double[] tfidfVector = new double[allTokens.size()];
        for (int i = 0; i < allTokens.size(); i++) {
            String token = allTokens.get(i);
            if (tfMap.containsKey(token)) {
                double idf = idfMap.getOrDefault(token, 0.0);
                tfidfVector[i] = tfMap.get(token) * idf;
            }
        }
        return tfidfVector;
    }

    private static void validateCreateTFIDFParameters(Map<String, Map<String, Object>> tokenizedFAQ,
                                                      Map<String, Double> idfMap,
                                                      List<String> allTokens) {
        validateTokenizedFAQ(tokenizedFAQ);
        if (idfMap == null || idfMap.isEmpty()) {
            throw new GlobalException(ErrorCode.MISSING_REQUEST_PARAMETER, "IDF map is empty or null.");
        }
        if (allTokens == null || allTokens.isEmpty()) {
            throw new GlobalException(ErrorCode.MISSING_REQUEST_PARAMETER, "Token list is empty or null.");
        }
    }

    private static void validateIDFAndTokenParameters(Map<String, Double> idfMap, List<String> allTokens) {
        if (idfMap == null || idfMap.isEmpty()) {
            throw new GlobalException(ErrorCode.MISSING_REQUEST_PARAMETER, "IDF map is empty or null.");
        }
        if (allTokens == null || allTokens.isEmpty()) {
            throw new GlobalException(ErrorCode.MISSING_REQUEST_PARAMETER, "Token list is empty or null.");
        }
    }
}
