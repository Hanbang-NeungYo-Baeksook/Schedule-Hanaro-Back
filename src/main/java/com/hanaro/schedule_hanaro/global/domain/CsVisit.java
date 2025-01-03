package com.hanaro.schedule_hanaro.global.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "CS_Visit")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CsVisit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cs_visit_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)  // N:1 관계 설정
	@JoinColumn(name = "branch_id", nullable = false, unique = true)  // 외래키 연결
	private Branch branch;

	@Column(name = "total_num", nullable = false, columnDefinition = "int default 0")
	private int totalNum;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	@Column(name = "wait_amount", nullable = false, columnDefinition = "int default 0")
	private int waitAmount;

	@Version
	private Long version;

	@Builder
	public CsVisit(
		Branch branch,
		int totalNum,
		LocalDate date
		// int waitAmount
	) {
		this.branch = branch;
		this.totalNum = totalNum;
		this.date = date;
		// this.waitAmount = waitAmount;
	}

	public void decreaseWaitAmount() {
		this.waitAmount--;
	}

	public void increase() {
		totalNum += 1;
		waitAmount += 1;
	}

	public void increaseTotalNum() {
		totalNum += 1;
	}
}
