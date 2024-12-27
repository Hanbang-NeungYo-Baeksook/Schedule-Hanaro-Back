package com.hanaro.schedule_hanaro.global.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hanaro.schedule_hanaro.customer.service.CsVisitService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class DataScheduler {
	private final CsVisitService csVisitService;

	@Scheduled(cron = "0 0 0 * * ?")
	public void csVisitInsert() {
		csVisitService.insertCsVisit();
	}
}
