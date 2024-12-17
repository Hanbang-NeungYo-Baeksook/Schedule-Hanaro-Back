package com.hanaro.schedule_hanaro.global.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;
import com.hanaro.schedule_hanaro.global.domain.enums.InquiryStatus;

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

	@Column(name = "content",length = 500, nullable = false)
	private String content;

	@Column(name = "inquiry_num", nullable = false)
	private Integer inquiryNum;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private Category category;

	@Enumerated(EnumType.STRING)
	@ColumnDefault("'PENDING'")
	@Column(name = "status", nullable = false)
	private InquiryStatus inquiryStatus = InquiryStatus.REGISTRATIONCOMPLETE;

	@Column(name = "tags", nullable = false)
	private String tags;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Builder
	public Inquiry(Customer customer, String content, int inquiryNum, Category category, InquiryStatus status, String tags, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.customer = customer;
		this.content = content;
		this.inquiryNum = inquiryNum;
		this.category = category;
		this.inquiryStatus = status;
		this.tags = tags;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}
