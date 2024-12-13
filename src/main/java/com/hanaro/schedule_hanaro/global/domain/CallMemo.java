package com.hanaro.schedule_hanaro.global.domain;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "Call_Memo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CallMemo {

	@Id
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "call_id", nullable = false, unique = true)
	private Call call;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id", nullable = false)
	private Admin admin;

	@Column(name = "content",length = 500, nullable = false)
	private String content;

	@Builder
	public CallMemo(Call call, Admin admin, String content) {
		this.call = call;
		this.admin = admin;
		this.content = content;
	}
}
