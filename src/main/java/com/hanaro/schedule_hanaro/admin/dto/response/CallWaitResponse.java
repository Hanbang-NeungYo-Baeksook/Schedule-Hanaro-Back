package com.hanaro.schedule_hanaro.admin.dto.response;

import java.util.List;

public record CallWaitResponse(
	CallInfoResponse progress,
	List<CallInfoResponse> waiting
) {
	public static CallWaitResponse of(
		final CallInfoResponse progress,
		final List<CallInfoResponse> waiting
	) {
		return new CallWaitResponse(progress, waiting);
	}
}
