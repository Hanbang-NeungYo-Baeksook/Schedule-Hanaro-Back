package com.hanaro.schedule_hanaro.global.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Inquiry_Response")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryResponse {

	@Id
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inquiry_id", nullable = false, unique = true)
	private Inquiry inquiry;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id", nullable = false)
	private Admin admin;

	@Column(name = "content", nullable = false)
	private String content;
	//
	// @Column(name = "created_at", nullable = false)
	// private LocalDateTime createdAt;

	@Builder
	public InquiryResponse(Inquiry inquiry, Admin admin, String content) {
		this.inquiry = inquiry;
		this.admin = admin;
		this.content = content;
		// this.createdAt = createdAt;
	}
}
