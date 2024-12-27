package com.hanaro.schedule_hanaro.customer.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.customer.dto.RegisterReservationDto;
import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.CsVisit;
import com.hanaro.schedule_hanaro.global.domain.enums.BranchType;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.BranchRepository;
import com.hanaro.schedule_hanaro.global.repository.CsVisitRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CsVisitService {
	private final CsVisitRepository csVisitRepository;
	private final BranchRepository branchRepository;

	@Scheduled(cron = "0 0 0 * * ?")
	@Transactional
	public void insertCsVisit() {
		List<Branch> bankList = branchRepository.findAllByBranchType(BranchType.BANK);
		List<CsVisit> csVisitList = bankList.stream().map(
			branch ->
				CsVisit.builder()
					.date(LocalDate.now())
					.branch(branch)
					.totalNum(0)
					.build()
		).toList();
		csVisitRepository.saveAll(csVisitList);
	}

	@Transactional // 트랜잭션 단위 분리
	public int increaseWait(RegisterReservationDto registerReservationDto) {
		CsVisit optimisticLock = csVisitRepository.findById(registerReservationDto.csVisitId())
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CS_VISIT));
		System.out.println(optimisticLock.getWaitAmount() + " " + optimisticLock.getTotalNum());
		optimisticLock.increase();
		System.out.println(optimisticLock.getWaitAmount() + " " + optimisticLock.getTotalNum());
		csVisitRepository.saveAndFlush(optimisticLock);

		return optimisticLock.getTotalNum();
	}
}
