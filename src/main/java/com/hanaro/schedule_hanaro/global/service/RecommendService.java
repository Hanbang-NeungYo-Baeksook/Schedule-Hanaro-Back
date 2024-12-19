package com.hanaro.schedule_hanaro.global.service;

import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.apache.commons.math3.linear.RealVector;
import com.hanaro.schedule_hanaro.customer.dto.response.RecommendDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.RecommendListResponse;
import com.hanaro.schedule_hanaro.global.domain.Recommend;
import com.hanaro.schedule_hanaro.global.repository.RecommendRepository;
import com.hanaro.schedule_hanaro.global.utils.FAQTokenizer;
import com.hanaro.schedule_hanaro.global.utils.TFIDFVectorizer;
import com.hanaro.schedule_hanaro.global.utils.TagRecommender;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;

@Service
public class RecommendService {
	public static final double COSINE_SIMILARITY_WEIGHT = 0.7;
	public static final double TFIDF_SIMILARITY_WEIGHT = 0.3;

	private final RecommendRepository recommendRepository;

	public RecommendService(RecommendRepository recommendRepository) {
		this.recommendRepository = recommendRepository;
	}

	public RecommendListResponse getRecommends(String query) {
		if (query == null || query.isEmpty()) {
			throw new GlobalException(ErrorCode.MISSING_REQUEST_PARAMETER, "Query is null or empty.");
		}

		List<Recommend> allRecommends = recommendRepository.findAll();
		if (allRecommends.isEmpty()) {
			throw new GlobalException(ErrorCode.NOT_FOUND_DATA, "No recommendations found in the database.");
		}

		List<String> allTokens = getAllUniqueTokens(allRecommends);
		List<String> questions = getQuestionList(allRecommends);
		Map<String, Double> idfMap = calculateIDF(questions);

		RealVector queryVector = TFIDFVectorizer.createTFIDFVectorForNewQuestion(query, idfMap, allTokens);
		List<String> recommendedTags = TagRecommender.recommendTagsForQuery(query);

		if (recommendedTags == null) {
			throw new GlobalException(ErrorCode.MISSING_REQUEST_PARAMETER, "Failed to recommend tags.");
		}

		Map<String, Map<String, Object>> tokenizedFAQ = convertRecommendsToTokenizedFAQ(allRecommends);
		Map<String, RealVector> tfidfVectors = TFIDFVectorizer.createTFIDFVectors(tokenizedFAQ, idfMap, allTokens);

		List<RecommendDetailResponse> recommendDetails = getTop3SimilarQuestionsTFIDF(
				tfidfVectors,
				queryVector,
				allRecommends
		);

		return RecommendListResponse.of(recommendDetails, recommendedTags);
	}

	private List<String> getAllUniqueTokens(List<Recommend> recommends) {
		return recommends.stream()
				.flatMap(recommend -> FAQTokenizer.tokenizeNewQuestion(recommend.getQuery()).stream())
				.distinct()
				.sorted()
				.collect(Collectors.toList());
	}

	private List<String> getQuestionList(List<Recommend> recommends) {
		return recommends.stream()
				.map(Recommend::getQuery)
				.collect(Collectors.toList());
	}

	private Map<String, Double> calculateIDF(List<String> questions) {
		if (questions == null || questions.isEmpty()) {
			throw new GlobalException(ErrorCode.MISSING_REQUEST_PARAMETER, "Questions list is null or empty.");
		}

		int totalDocuments = questions.size();
		Map<String, Integer> documentFrequency = new HashMap<>();

		for (String question : questions) {
			Set<String> uniqueTokensInDoc = new HashSet<>(FAQTokenizer.tokenizeNewQuestion(question));
			for (String token : uniqueTokensInDoc) {
				documentFrequency.merge(token, 1, Integer::sum);
			}
		}

		return documentFrequency.entrySet().stream()
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						entry -> Math.log((double) totalDocuments / (entry.getValue() + 1.0))
				));
	}

	private Map<String, Map<String, Object>> convertRecommendsToTokenizedFAQ(List<Recommend> recommends) {
		if (recommends == null || recommends.isEmpty()) {
			throw new GlobalException(ErrorCode.NOT_FOUND_DATA, "Recommendations are empty.");
		}

		Map<String, Map<String, Object>> tokenizedFAQ = new HashMap<>();

		for (Recommend recommend : recommends) {
			if (recommend == null || recommend.getQuery() == null) {
				throw new GlobalException(ErrorCode.MISSING_REQUEST_PARAMETER, "Recommend or query is null.");
			}

			Map<String, Object> entry = new HashMap<>();
			entry.put("questionTokens", FAQTokenizer.tokenizeNewQuestion(recommend.getQuery()));
			entry.put("fullAnswer", recommend.getResponse());

			tokenizedFAQ.put(recommend.getQuery(), entry);
		}

		return tokenizedFAQ;
	}

	private List<RecommendDetailResponse> getTop3SimilarQuestionsTFIDF(
			Map<String, RealVector> tfidfVectors,
			RealVector queryVector,
			List<Recommend> recommends
	) {
		if (tfidfVectors == null || queryVector == null || recommends == null) {
			throw new GlobalException(ErrorCode.MISSING_REQUEST_PARAMETER, "Invalid input parameters.");
		}

		Map<String, Double> similarityScores = calculateSimilarityScores(
				tfidfVectors,
				queryVector
		);

		return similarityScores.entrySet().stream()
				.sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
				.limit(3)
				.map(entry -> createRecommendDetailResponse(entry, recommends))
				.collect(Collectors.toList());
	}

	private Map<String, Double> calculateSimilarityScores(
			Map<String, RealVector> tfidfVectors,
			RealVector queryVector
	) {
		Map<String, Double> similarityScores = new HashMap<>();

		for (Map.Entry<String, RealVector> entry : tfidfVectors.entrySet()) {
			String query = entry.getKey();
			RealVector questionVector = entry.getValue();

			if (queryVector.getDimension() != questionVector.getDimension()) {
				throw new GlobalException(
						ErrorCode.WRONG_REQUEST_PARAMETER,
						"Vector dimensions do not match for query: " + query
				);
			}

			double cosineSimilarity = TFIDFVectorizer.calculateCosineSimilarity(queryVector, questionVector);
			double tfidfSimilarity = 1.0 / (1.0 + queryVector.subtract(questionVector).getNorm());
			double hybridSimilarity = (RecommendService.COSINE_SIMILARITY_WEIGHT * cosineSimilarity) +
					(RecommendService.TFIDF_SIMILARITY_WEIGHT * tfidfSimilarity);

			similarityScores.put(query, hybridSimilarity);
		}

		return similarityScores;
	}

	private RecommendDetailResponse createRecommendDetailResponse(
			Map.Entry<String, Double> entry,
			List<Recommend> recommends
	) {
		String query = entry.getKey();
		double similarity = entry.getValue();

		Recommend matchedRecommend = recommends.stream()
				.filter(recommend -> recommend.getQuery().equals(query))
				.findFirst()
				.orElseThrow(() -> new GlobalException(
						ErrorCode.NOT_FOUND_DATA,
						"Recommend not found for query: " + query
				));

		return RecommendDetailResponse.of(
				matchedRecommend.getId(),
				matchedRecommend.getQuery(),
				matchedRecommend.getResponse(),
				(int) (similarity * 100)
		);
	}
}