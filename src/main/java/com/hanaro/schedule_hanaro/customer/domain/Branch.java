package com.hanaro.schedule_hanaro.customer.domain;

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
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Branch {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "branch_id")
	private Long id;

	@Column(name = "branch_num")
	private String branchNum;
	@Column(name = "branch_name")
	private String name;
	@Column(name = "branch_type")
	private String type;
	@Column(name = "x_position")
	private String xPosition;
	@Column(name = "y_position")
	private String yPosition;
	@Column(name = "address")
	private String address;
	@Column(name = "tel")
	private String tel;
	@Column(name = "business_time")
	private String businessTime;

	public static Branch of(
		String branchNum,
		String name,
		String type,
		String xPosition,
		String yPosition,
		String address,
		String tel,
		String businessTime
	) {
		return Branch.builder()
			.branchNum(branchNum)
			.name(name)
			.type(type)
			.xPosition(xPosition)
			.yPosition(yPosition)
			.address(address)
			.tel(tel)
			.businessTime(businessTime)
			.build();
	}
}
