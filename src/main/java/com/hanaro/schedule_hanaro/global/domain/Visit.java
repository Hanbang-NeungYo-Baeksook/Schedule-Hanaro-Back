package com.hanaro.schedule_hanaro.global.domain;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.hanaro.schedule_hanaro.global.domain.enums.Status;

import jakarta.persistence.*;
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
	@JoinColumn(name = "branch_id", nullable = false)
	private Branch branch;

	@Column(name = "visit_date", nullable = false)
	private LocalDate visitDate;

	@Column(name = "num", nullable = false)
	private int num;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status;

	@Column(name = "started_at")
	private LocalDateTime startedAt;

	@Column(name = "ended_at")
	private LocalDateTime endedAt;

	@Column(name = "content",length = 500)
	private String content;

	@Column(name = "tags", length = 50)
	private String tags;

	@Builder
	public Visit(
		Customer customer,
		Branch branch,
		LocalDate visitDate,
		int num,
		Status status,
		LocalDateTime startedAt,
		LocalDateTime endedAt,
		String content,
		String tags
	) {
		this.customer = customer;
		this.branch = branch;
		this.visitDate = visitDate;
		this.num = num;
		this.status = status;
		this.startedAt = startedAt;
		this.endedAt = endedAt;
		this.content = content;
		this.tags = tags;
	}
}
