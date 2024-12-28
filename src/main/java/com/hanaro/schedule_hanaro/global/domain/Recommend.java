package com.hanaro.schedule_hanaro.global.domain;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Recommend")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recommend {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "recommend_id")
	private Long id;

	@Column(length = 500, nullable = false)
	private String query;

	@Column(length = 1024, nullable = false)
	private String response;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Category category;

	@Column(name = "query_vector", length = 2000, nullable = false)
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
