package com.hanaro.schedule_hanaro.global.domain;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CsCall {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cs_call_id")
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)  // 1:1 관계 설정
	@JoinColumn(name = "branch_id", nullable = false)  // 외래키 연결
	private Branch branch;

	@Column(name = "current_num", nullable = false, columnDefinition = "int default 0")
	private int currentNum;

	@Column(name = "total_num", nullable = false, columnDefinition = "int default 0")
	private int totalNum;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	@Column(name = "wait_amount", nullable = false, columnDefinition = "int default 0")
	private int waitAmount;

	@Builder
	public CsCall (
		Branch branch,
		int currentNum,
		int totalNum,
		LocalDate date,
		int waitAmount
	) {
		this.branch = branch;
		this.currentNum = currentNum;
		this.totalNum = totalNum;
		this.date = date;
		this.waitAmount = waitAmount;
	}
}
