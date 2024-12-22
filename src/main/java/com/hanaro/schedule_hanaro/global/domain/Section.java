package com.hanaro.schedule_hanaro.global.domain;

import org.hibernate.annotations.Fetch;

import com.hanaro.schedule_hanaro.global.domain.enums.SectionType;

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
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Section")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Section {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "section_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branch_id", nullable = false)
	private Branch branch;

	@Column(name = "section_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private SectionType sectionType;

	@Column(name = "current_num", nullable = false, columnDefinition = "int unsigned default 0")
	private Integer currentNum;

	@Column(name = "wait_amount", nullable = false, columnDefinition = "int unsigned default 0")
	private Integer waitAmount;

	@Column(name = "wait_time", nullable = false, columnDefinition = "int unsigned default 0")
	private Integer waitTime;

	@Version
	private Long version;

	@Builder
	public Section(
		Branch branch,
		SectionType sectionType
	) {
		this.branch = branch;
		this.sectionType = sectionType;
	}

	public void increase(Integer amount) {
		this.currentNum += 1;
		this.waitAmount += 1;
		this.waitTime += amount;
	}

	public void decrease(Integer amount) {
		this.waitAmount -= 1;
		this.waitTime -= amount;
	}

	public void updateStatusPendingToProgress(int num, Integer amount){
		this.currentNum  = num;
		this.waitAmount -= 1;
		this.waitTime = Math.max(this.waitTime - amount, 0);
	}
}
