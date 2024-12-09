package com.hanaro.schedule_hanaro.global.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Branch {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "branch_id")
	private Long id;

	@Column(name = "branch_num", nullable = false)
	private String branchNum;
	@Column(name = "branch_name", nullable = false)
	private String name;
	@Column(name = "branch_type", nullable = false)
	private String type;
	@Column(name = "x_position", nullable = false)
	private String xPosition;
	@Column(name = "y_position", nullable = false)
	private String yPosition;
	@Column(name = "address", nullable = false)
	private String address;
	@Column(name = "tel")
	private String tel;
	@Column(name = "business_time", nullable = false)
	private String businessTime;

	@Builder
	public  Branch (
		String branchNum,
		String name,
		String type,
		String xPosition,
		String yPosition,
		String address,
		String tel,
		String businessTime
	) {
		this.branchNum = branchNum;
		this.name = name;
		this.type = type;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.address = address;
		this.tel = tel;
		this.businessTime = businessTime;
	}
}
