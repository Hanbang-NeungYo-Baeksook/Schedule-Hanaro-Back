package com.hanaro.schedule_hanaro.global.domain;
import java.time.LocalDateTime;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Inquiry")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inquiry_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;
	//
	// @Column(name = "title", nullable = false)
	// private String title;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private Category category;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "tags", nullable = false)
	private String tags;

	@Builder
	public Inquiry(Customer customer, Category category, String content, LocalDateTime createdAt, String tags) {
		this.customer = customer;
		// this.title = title;
		this.category = category;
		this.content = content;
		this.createdAt = createdAt;
		this.tags = tags;
	}
}
