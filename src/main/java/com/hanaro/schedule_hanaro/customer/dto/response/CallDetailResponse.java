package com.hanaro.schedule_hanaro.customer.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import java.util.List;

@Builder
public record CallDetailResponse(
	@JsonProperty("call_id") Long callId,
	@JsonProperty("customer_name") String customerName,
	@JsonProperty("call_date") String callDate,
	@JsonProperty("call_time") String callTime,
	@JsonProperty("call_num") int callNum,
	String category,
	String status,
	String content,
	List<String> tags,
	@JsonProperty("wait_num") int waitNum,
	@JsonProperty("estimated_wait_time") int estimatedWaitTime
) {}
