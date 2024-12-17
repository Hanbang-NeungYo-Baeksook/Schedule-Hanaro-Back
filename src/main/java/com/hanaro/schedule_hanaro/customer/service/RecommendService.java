package com.hanaro.schedule_hanaro.customer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.customer.dto.response.RecommendDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.RecommendListResponse;
import com.hanaro.schedule_hanaro.global.repository.RecommendRepository;
import com.hanaro.schedule_hanaro.global.domain.Recommend;

@Service
public class RecommendService {
	public static final List<String> DICTIONARY = List.of("예금", "적금", "가입", "회원가입", "펀드");
	public static final List<Integer> TAGS = List.of(0, 1, 4);
	private final RecommendRepository recommendRepository;

	public RecommendService(RecommendRepository recommendRepository) {
		this.recommendRepository = recommendRepository;
	}

	public RecommendListResponse getRecommends(String query) {
		// TODO: Convert Query to Vector with Dictionary
		String vector = convertQueryToVector(query);

		// TODO: Get Tags from Query Vector
		List<String> tags = getTags(vector);

		// TODO: Calculate Similarity with Dataset and Get Top 3
		List<RecommendDetailResponse> recommendDetailResponses = getTop3SimilarityRecommends(vector);
		return RecommendListResponse.of(recommendDetailResponses, tags);
	}

	private int getSimilarity(String vector, String queryVector) {
		return 90;
	}

	private List<RecommendDetailResponse> getTop3SimilarityRecommends(String vector) {
		List<Recommend> recommendList = recommendRepository.findAll();
		List<RecommendDetailResponse> recommendDetailResponses = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			Recommend recommend = recommendList.get(i);
			int similarity = getSimilarity(vector, recommend.getQueryVector());
			RecommendDetailResponse recommendDetailResponse = RecommendDetailResponse.of(
				recommend.getId(), recommend.getQuery(), recommend.getResponse(), similarity
			);
			recommendDetailResponses.add(recommendDetailResponse);
		}
		return recommendDetailResponses;
	}

	private List<String> getTags(String vector) {
		final List<String> tags = new ArrayList<>();
		for (Integer tag : TAGS) {
			if (vector.charAt(tag) == '1') {
				tags.add(DICTIONARY.get(tag));
			}
		}
		return tags;
	}

	private String convertQueryToVector(String query) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < DICTIONARY.size(); i++) {
			builder.append(Math.random() > 0.5 ? "1" : "0");
		}
		return builder.toString();
	}
}
