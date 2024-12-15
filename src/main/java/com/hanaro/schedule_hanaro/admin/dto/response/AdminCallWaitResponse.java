package com.hanaro.schedule_hanaro.admin.dto.response;

import java.util.List;

public record AdminCallWaitResponse(
	AdminCallInfoResponse progress,
	List<AdminCallInfoResponse> waiting
) {
	public static AdminCallWaitResponse of(
		final AdminCallInfoResponse progress,
		final List<AdminCallInfoResponse> waiting
	) {
		return new AdminCallWaitResponse(progress, waiting);
	}
}
