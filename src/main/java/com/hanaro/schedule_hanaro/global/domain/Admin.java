package com.hanaro.schedule_hanaro.global.domain;

import com.hanaro.schedule_hanaro.global.domain.enums.Role;

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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Admin")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "admin_id")
	private Long id;

	@Column(name = "auth_id", unique = true, nullable = false)
	private String authId;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branch_id")
	private Branch branch;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Builder
	public Admin(
		String authId,
		String password,
		String name,
		Branch branch
	) {
		this.authId = authId;
		this.password = password;
		this.name = name;
		this.branch = branch;
		this.role = Role.ADMIN;
	}
}
