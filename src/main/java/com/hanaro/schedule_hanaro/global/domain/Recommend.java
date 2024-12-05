package com.hanaro.schedule_hanaro.global.domain;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recommend {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recommend_id")
	private Long id;

	@Column(nullable = false)
	private String query;

	@Column(nullable = false)
	private String response;

	@Column(nullable = false)
	private Category category;

	@Column(name = "query_vector", nullable = false)
	private String queryVector;

	@Builder
	public Recommend(
		String query,
		String response,
		Category category,
		String queryVector
	) {
		this.query = query;
		this.response = response;
		this.category = category;
		this.queryVector = queryVector;
	}
}
