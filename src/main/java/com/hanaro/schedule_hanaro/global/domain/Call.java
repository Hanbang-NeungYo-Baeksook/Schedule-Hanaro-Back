package com.hanaro.schedule_hanaro.global.domain;

import java.time.LocalDateTime;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Call {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "call_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;

	@Column(name = "call_date", nullable = false)
	private LocalDateTime callDate;

	@Column(name = "call_num", nullable = false)
	private int callNum;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private Category category;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private Status status;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "started_at")
	private LocalDateTime startedAt;

	@Column(name = "ended_at")
	private LocalDateTime endedAt;

	@Column(name = "tags", nullable = false)
	private String tags;

	@Builder
	public Call(Customer customer, LocalDateTime callDate, int callNum, Category category, Status status, String content, LocalDateTime startedAt, LocalDateTime endedAt, String tags) {
		this.customer = customer;
		this.callDate = callDate;
		this.callNum = callNum;
		this.category = category;
		this.status = status;
		this.content = content;
		this.startedAt = startedAt;
		this.endedAt = endedAt;
		this.tags = tags;
	}
}
