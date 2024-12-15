package com.hanaro.schedule_hanaro.customer.dto.request;

import lombok.Builder;

@Builder
public record VisitCreateRequest(Long customerId, Long branchId, String content) {
}
