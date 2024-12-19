package com.hanaro.schedule_hanaro.global.utils;

import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class TagRecommender {
    private static Word2Vec word2VecModel;

    // Predefined list of tags
    private static final List<String> PREDEFINED_TAGS = Arrays.asList(
            "인증", "확인", "인증서", "계좌", "통장", "변경", "뱅킹", "금융", "대출", "한도",
            "신청", "등록", "조회", "결과", "예금", "적금", "고객", "개인", "회원", "비밀번호",
            "보안", "소득", "증명서", "증빙", "서류", "자료", "거래", "입금", "금리", "계약",
            "약정", "투자", "자금", "문의", "상담", "우대", "포인트", "지점", "전화", "모바일",
            "기간", "조건", "업무", "조치", "제출", "화면", "입력", "상태", "서비스", "비대면","미성년자"
    );

    // Static initializer to load Word2Vec model
    static {
        try {
            URL resourceUrl = TagRecommender.class.getClassLoader().getResource("ko_google.bin");
            if (resourceUrl == null) {
                throw new FileNotFoundException("Word2Vec 모델 리소스를 찾을 수 없습니다.");
            }

            String modelPath = new File(resourceUrl.toURI()).getAbsolutePath();
            word2VecModel = WordVectorSerializer.readWord2VecModel(new File(modelPath));

            System.out.println("Word2Vec 모델 로드 성공. 어휘 크기: " + word2VecModel.vocab().numWords());
        } catch (Exception e) {
            System.err.println("Word2Vec 모델 로드 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 주어진 쿼리에 대해 추천 태그를 반환합니다.
     *
     * @param query 사용자 입력 쿼리
     * @return 추천된 태그 리스트
     */
    public static List<String> recommendTagsForQuery(String query) {
        // 1. 질문 토큰화
        List<String> questionTokens = FAQTokenizer.tokenizeNewQuestion(query);
        System.out.println("질문 토큰화 결과: " + questionTokens);

        // 2. 토큰 벡터화 및 유사도 계산
        Map<String, Double> tagSimilarities = calculateTagSimilarities(questionTokens);

        // 3. 유사도 기준으로 상위 3개 태그 선택
        List<Map.Entry<String, Double>> sortedTags = tagSimilarities.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(3)
                .collect(Collectors.toList());

        // 4. 디버그 정보 출력
        printTagSimilarities(sortedTags);

        // 5. 상위 3개 태그 반환
        return sortedTags.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 토큰들의 평균 벡터를 계산합니다.
     *
     * @param tokens 토큰 리스트
     * @return 평균 벡터
     */
    private static INDArray calculateAverageVector(List<String> tokens) {
        List<INDArray> validVectors = new ArrayList<>();

        // 유효한 벡터만 수집
        for (String token : tokens) {
            double[] wordVectorArray = word2VecModel.getWordVector(token);
            if (wordVectorArray != null) {
                validVectors.add(Nd4j.create(wordVectorArray));
            }
        }

        // 유효한 벡터가 없는 경우
        if (validVectors.isEmpty()) {
            return Nd4j.zeros(word2VecModel.getLayerSize());
        }

        // 평균 벡터 계산
        INDArray averageVector = validVectors.get(0);
        for (int i = 1; i < validVectors.size(); i++) {
            averageVector.addi(validVectors.get(i));
        }
        averageVector.divi(validVectors.size());

        return averageVector;
    }

    /**
     * 태그와 질문 벡터 간 유사도를 계산합니다.
     *
     * @param tokens 질문 토큰
     * @return 태그별 유사도 맵
     */
    private static Map<String, Double> calculateTagSimilarities(List<String> tokens) {
        // 모델이나 토큰이 없는 경우 빈 맵 반환
        if (word2VecModel == null || tokens.isEmpty()) {
            return new HashMap<>();
        }

        // 질문 평균 벡터 계산
        INDArray questionVector = calculateAverageVector(tokens);

        // 태그별 유사도 계산
        Map<String, Double> tagSimilarities = new HashMap<>();
        for (String tag : PREDEFINED_TAGS) {
            double[] tagVectorArray = word2VecModel.getWordVector(tag);
            if (tagVectorArray == null) continue;

            INDArray tagVector = Nd4j.create(tagVectorArray);
            double similarity = calculateCosineSimilarity(questionVector, tagVector);
            tagSimilarities.put(tag, similarity);
        }

        return tagSimilarities;
    }

    /**
     * 코사인 유사도를 계산합니다.
     *
     * @param vec1 첫 번째 벡터
     * @param vec2 두 번째 벡터
     * @return 코사인 유사도
     */
    private static double calculateCosineSimilarity(INDArray vec1, INDArray vec2) {
        double dotProduct = Nd4j.getBlasWrapper().dot(vec1, vec2);
        double normVec1 = vec1.norm2Number().doubleValue();
        double normVec2 = vec2.norm2Number().doubleValue();

        // 0으로 나누는 것을 방지
        if (normVec1 == 0 || normVec2 == 0) {
            return 0.0;
        }

        return dotProduct / (normVec1 * normVec2);
    }

    /**
     * 태그 유사도 정보를 출력합니다.
     *
     * @param sortedTags 정렬된 태그 엔트리
     */
    private static void printTagSimilarities(List<Map.Entry<String, Double>> sortedTags) {
        System.out.println("추천 태그 및 유사도:");
        for (Map.Entry<String, Double> entry : sortedTags) {
            System.out.printf("태그: %s, 유사도: %.4f%n", entry.getKey(), entry.getValue());
        }
    }
}
