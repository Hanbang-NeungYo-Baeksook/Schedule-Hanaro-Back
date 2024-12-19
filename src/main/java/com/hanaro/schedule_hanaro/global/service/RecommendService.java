package com.hanaro.schedule_hanaro.global.service;

import com.hanaro.schedule_hanaro.customer.dto.response.RecommendDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.RecommendListResponse;
import com.hanaro.schedule_hanaro.global.domain.Recommend;
import com.hanaro.schedule_hanaro.global.repository.RecommendRepository;

import com.hanaro.schedule_hanaro.global.utils.FAQTokenizer;
import com.hanaro.schedule_hanaro.global.utils.TFIDFVectorizer;
import com.hanaro.schedule_hanaro.global.utils.TagRecommender;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendService {

	private final RecommendRepository recommendRepository;

	public RecommendService(RecommendRepository recommendRepository) {
		this.recommendRepository = recommendRepository;
	}

	/**
	 * 질문 쿼리를 받아서 추천 답변과 태그를 함께 반환합니다.
	 *
	 * @param query 사용자 입력 쿼리
	 * @return 추천 리스트 응답
	 */
	public RecommendListResponse getRecommends(String query) {
		List<Recommend> allRecommends = recommendRepository.findAll();

		// 1. 모든 추천 질문에서 고유한 토큰 집합 생성
		List<String> allTokens = allRecommends.stream()
				.flatMap(recommend -> FAQTokenizer.tokenizeNewQuestion(recommend.getQuery()).stream())
				.distinct()
				.sorted()
				.collect(Collectors.toList());

		// 2. 추천 질문 리스트
		List<String> questions = allRecommends.stream()
				.map(Recommend::getQuery)
				.collect(Collectors.toList());

		// 3. IDF 계산
		Map<String, Double> idfMap = calculateIDF(questions);

		// 4. 질문 쿼리 벡터화
		RealVector newQuestionVector = TFIDFVectorizer.createTFIDFVectorForNewQuestion(query, idfMap, allTokens);

		// 5. 추천 태그 얻기
		List<String> recommendedTags = TagRecommender.recommendTagsForQuery(query);

		// 6. 데이터베이스에서 유사도 높은 추천 답변 가져오기
		Map<String, Map<String, Object>> tokenizedFAQ = convertRecommendsToTokenizedFAQ(allRecommends);
		Map<String, RealVector> tfidfVectors = TFIDFVectorizer.createTFIDFVectors(tokenizedFAQ, idfMap, allTokens);
		List<RecommendDetailResponse> recommendDetails = getTop3SimilarQuestionsTFIDF(tfidfVectors, newQuestionVector, allRecommends, 0.7, 0.3);

		// 7. 최종 응답 구성 (태그 추가)
		return RecommendListResponse.of(recommendDetails, recommendedTags);
	}

	// IDF 계산
	public Map<String, Double> calculateIDF(List<String> questions) {
		int totalDocuments = questions.size();
		Map<String, Integer> documentFrequency = new HashMap<>();

		for (String question : questions) {
			Set<String> uniqueTokensInDoc = new HashSet<>(FAQTokenizer.tokenizeNewQuestion(question));
			for (String token : uniqueTokensInDoc) {
				documentFrequency.put(token, documentFrequency.getOrDefault(token, 0) + 1);
			}
		}

		return documentFrequency.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						entry -> Math.log((double) totalDocuments / (entry.getValue() + 1.0))
				));
	}

	// 추천 리스트를 Map<String, Map<String, Object>>로 변환
	private Map<String, Map<String, Object>> convertRecommendsToTokenizedFAQ(List<Recommend> recommends) {
		Map<String, Map<String, Object>> tokenizedFAQ = new HashMap<>();

		for (Recommend recommend : recommends) {
			Map<String, Object> entry = new HashMap<>();
			entry.put("questionTokens", FAQTokenizer.tokenizeNewQuestion(recommend.getQuery())); // 질문 토큰화
			entry.put("fullAnswer", recommend.getResponse()); // 전체 답변 추가

			tokenizedFAQ.put(recommend.getQuery(), entry);
		}

		return tokenizedFAQ;
	}

	public static List<RecommendDetailResponse> getTop3SimilarQuestionsTFIDF(
			Map<String, RealVector> tfidfVectors,
			RealVector newQuestionTFIDFVector,
			List<Recommend> recommends,
			double cosineSimilarityWeight,
			double tfidfSimilarityWeight
	) {
		Map<String, Double> similarityScores = new HashMap<>();

		for (String query : tfidfVectors.keySet()) {
			RealVector questionVector = tfidfVectors.get(query);

			// 벡터 크기 체크
			if (newQuestionTFIDFVector.getDimension() != questionVector.getDimension()) {
				throw new IllegalArgumentException("Vector dimensions do not match for query: " + query);
			}

			// 코사인 유사도 계산
			double cosineSimilarity = TFIDFVectorizer.calculateCosineSimilarity(newQuestionTFIDFVector, questionVector);

			// TF-IDF 유사도 계산
			double tfidfSimilarity = 1.0 / (1.0 + newQuestionTFIDFVector.subtract(questionVector).getNorm());

			// 하이브리드 유사도
			double hybridSimilarity = (cosineSimilarityWeight * cosineSimilarity) + (tfidfSimilarityWeight * tfidfSimilarity);
			similarityScores.put(query, hybridSimilarity);
		}

		// 상위 유사 질문 3개 선택 및 반환
		return similarityScores.entrySet().stream()
				.sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
				.limit(3)
				.map(entry -> {
					String query = entry.getKey();
					double similarity = entry.getValue();

					Recommend matchedRecommend = recommends.stream()
							.filter(recommend -> recommend.getQuery().equals(query))
							.findFirst()
							.orElseThrow(() -> new IllegalArgumentException("Recommend not found for query: " + query));

					return RecommendDetailResponse.of(
							matchedRecommend.getId(),
							matchedRecommend.getQuery(),
							matchedRecommend.getResponse(),
							(int) (similarity*100)
					);
				})
				.collect(Collectors.toList());
	}
}
