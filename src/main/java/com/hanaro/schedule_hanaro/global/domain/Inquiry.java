package com.hanaro.schedule_hanaro.global.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
public class Inquiry extends BaseEntity{

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id", nullable = false)
	private Admin admin;

	@Enumerated(EnumType.STRING)
	@Column(name = "category", nullable = false)
	private Category category;

	@Enumerated(EnumType.STRING)
	@ColumnDefault("'PENDING'")
	@Column(name = "status", nullable = false)
	private InquiryStatus inquiryStatus = InquiryStatus.PENDING;

	@Column(name = "tags", nullable = false)
	private String tags;

	@Column(name = "query_vector", nullable = false)
	private String queryVector;

	@Builder
	public Inquiry(Customer customer, Admin admin, String content, int inquiryNum, Category category, InquiryStatus status, String tags, String queryVector) {
		this.customer = customer;
		this.admin = admin;
		this.content = content;
		this.inquiryNum = inquiryNum;
		this.category = category;
		this.inquiryStatus = status;
		this.tags = tags;
		this.queryVector = queryVector;
	}

	public void setStatus(InquiryStatus status) {
		this.inquiryStatus = status;
	}
}
