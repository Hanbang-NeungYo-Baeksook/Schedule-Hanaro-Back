package com.hanaro.schedule_hanaro.admin.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanaro.schedule_hanaro.global.domain.Customer;

import lombok.Builder;

@Builder
public record AdminCustomerListResponse(
	List<CustomerData> customers,
	@JsonProperty("current_page") Integer currentPage,
	@JsonProperty("page_size") Integer pageSize,
	@JsonProperty("total_items") Long totalItems,
	@JsonProperty("total_pages") Integer totalPages
) {

	@Builder
	public record CustomerData(
		@JsonProperty("customer_name") String customerName,
		@JsonProperty("phone_number") String phoneNumber,
		@JsonProperty("birth_date") String birthDate,
		String email
	) {
		public static CustomerData of(Customer customer) {
			return CustomerData.builder()
				.customerName(customer.getName())
				.phoneNumber(customer.getPhoneNum())
				.birthDate(customer.getBirth().toString())
				.email(customer.getAuthId())
				.build();
		}
	}

	public static AdminCustomerListResponse from(
		List<CustomerData> customers, Integer currentPage, Integer pageSize, Long totalItems, Integer totalPages) {
		return AdminCustomerListResponse.builder()
			.customers(customers)
			.currentPage(currentPage)
			.pageSize(pageSize)
			.totalItems(totalItems)
			.totalPages(totalPages)
			.build();
	}
}
