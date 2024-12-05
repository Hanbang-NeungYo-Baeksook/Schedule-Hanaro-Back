package com.hanaro.schedule_hanaro.global.domain;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CallMemo {

	@Id
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "call_id", nullable = false, unique = true)
	private Call call;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id", nullable = false)
	private Admin admin;

	@Column(name = "contents", nullable = false)
	private String contents;

	@Builder
	public CallMemo(Call call, Admin admin, String contents) {
		this.call = call;
		this.admin = admin;
		this.contents = contents;
	}
}
