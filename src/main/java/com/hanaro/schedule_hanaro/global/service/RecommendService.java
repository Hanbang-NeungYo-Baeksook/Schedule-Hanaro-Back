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

import static com.hanaro.schedule_hanaro.global.utils.TFIDFVectorizer.createTFIDFVectors;

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

		List<String> allTokens = allRecommends.stream()
				.flatMap(recommend -> FAQTokenizer.tokenizeNewQuestion(recommend.getQuery()).stream())
				.distinct()
				.sorted()
				.collect(Collectors.toList());

		List<String> questions = allRecommends.stream()
				.map(Recommend::getQuery)
				.collect(Collectors.toList());



		// 1. 질문 쿼리 벡터화
		RealVector newQuestionVector = TFIDFVectorizer.createTFIDFVectorForNewQuestion(query,calculateIDF(questions),allTokens);

		// 2. 추천 태그 얻기
		List<String> recommendedTags = TagRecommender.recommendTagsForQuery(query);

		// 3. 데이터베이스에서 유사도 높은 추천 답변 가져오기
//		Map<String, RealVector> tfidfVectors= allRecommends.stream().map()

		List<RecommendDetailResponse> recommendDetails = getTop3SimilarQuestionsTFIDF( createTFIDFVectors(allRecommends),newQuestionVector,allRecommends,0.7,0.3);

		// 4. 최종 응답 구성 (태그 추가)
		return RecommendListResponse.of(recommendDetails, recommendedTags);
	}




	// 내부 클래스: Recommend와 유사도 점수를 함께 저장
	private static class RecommendWithSimilarity {
		Recommend recommend;
		double similarity;

		RecommendWithSimilarity(Recommend recommend, double similarity) {
			this.recommend = recommend;
			this.similarity = similarity;
		}
	}

	public Map<String, Double> calculateIDF(List<String> questions) {
		int totalDocuments = questions.size();
		Map<String, Integer> documentFrequency = new HashMap<>();

		// 각 질문을 토큰화하고 토큰의 문서 빈도 계산
		for (String question : questions) {
			Set<String> uniqueTokensInDoc = new HashSet<>(FAQTokenizer.tokenizeNewQuestion(question)); // 토큰화 및 중복 제거
			for (String token : uniqueTokensInDoc) {
				documentFrequency.put(token, documentFrequency.getOrDefault(token, 0) + 1);
			}
		}

		// IDF 계산: log(총 문서 수 / 해당 토큰이 등장하는 문서 수)
		return documentFrequency.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						entry -> Math.log((double) totalDocuments / (entry.getValue() + 1.0)) // +1.0은 smoothing
				));
	}

	public static List<RecommendDetailResponse> getTop3SimilarQuestionsTFIDF(
			Map<String, RealVector> tfidfVectors,
			RealVector newQuestionTFIDFVector,
			List<Recommend> recommends, // Recommend 리스트 추가
			double cosineSimilarityWeight,
			double tfidfSimilarityWeight
	) {
		Map<String, Double> similarityScores = new HashMap<>();

		// 유사도 계산
		for (String query : tfidfVectors.keySet()) {
			RealVector questionVector = tfidfVectors.get(query);

			// 코사인 유사도
			double cosineSimilarity = TFIDFVectorizer.calculateCosineSimilarity(
					newQuestionTFIDFVector,
					questionVector
			);

			// TF-IDF 유사도
			double tfidfSimilarity = 1.0 / (1.0 + newQuestionTFIDFVector.subtract(questionVector).getNorm());

			// 가중치를 적용한 하이브리드 유사도
			double hybridSimilarity = (cosineSimilarityWeight * cosineSimilarity) +
					(tfidfSimilarityWeight * tfidfSimilarity);

			similarityScores.put(query, hybridSimilarity);
		}

		// 상위 유사 질문 3개 선택 및 반환
		return similarityScores.entrySet().stream()
				.sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
				.limit(3)
				.map(entry -> {
					String query = entry.getKey();
					double similarity = entry.getValue();

					// Recommend 리스트에서 query에 해당하는 Recommend 찾기
					Recommend matchedRecommend = recommends.stream()
							.filter(recommend -> recommend.getQuery().equals(query))
							.findFirst()
							.orElseThrow(() -> new IllegalArgumentException("Recommend not found for query: " + query));

					// RecommendDetailResponse로 매핑
					return RecommendDetailResponse.of(
							matchedRecommend.getId(),
							matchedRecommend.getQuery(),
							matchedRecommend.getResponse(),
                            (int) similarity
                    );
				})
				.collect(Collectors.toList());
	}



}
