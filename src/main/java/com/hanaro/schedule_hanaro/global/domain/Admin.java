package com.hanaro.schedule_hanaro.global.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="admin_id")
	private Long id;

	@Column(name = "auth_id")
	private String authId;
	private String password;

	@ManyToOne
	@JoinColumn(name = "branch_id")
	private Branch branch;

	@Builder
	public Admin(
		String authId,
		String password,
		Branch branch
	){
		this.authId = authId;
		this.password = password;
		this.branch = branch;
	}
}
