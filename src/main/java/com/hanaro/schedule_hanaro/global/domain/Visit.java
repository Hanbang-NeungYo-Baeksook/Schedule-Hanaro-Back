package com.hanaro.schedule_hanaro.global.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Visit")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Visit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "visit_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "section_id", nullable = false)
	private Section section;

	@Column(name = "visit_date", nullable = false)
	private LocalDate visitDate;

	@Column(name = "num", nullable = false)
	private int num;

	@Enumerated(EnumType.STRING)
	@ColumnDefault("'PENDING'")
	@Column(name = "status", nullable = false)
	private Status status = Status.PENDING;

	@Column(name = "started_at")
	private LocalDateTime startedAt;

	@Column(name = "ended_at")
	private LocalDateTime endedAt;

	@Column(name = "content", length = 500)
	private String content;

	@Column(name = "tags", length = 50)
	private String tags;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private Category category;

	@Builder
	public Visit(
		Customer customer,
		Section section,
		LocalDate visitDate,
		int num,
		LocalDateTime startedAt,
		LocalDateTime endedAt,
		String content,
		String tags,
		Category category
	) {

		this.customer = customer;
		this.section = section;
		this.visitDate = visitDate;
		this.num = num;
		this.startedAt = startedAt;
		this.endedAt = endedAt;
		this.content = content;
		this.tags = tags;
		this.category = category;
	}
}
