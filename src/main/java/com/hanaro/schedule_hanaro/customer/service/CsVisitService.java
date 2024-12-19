package com.hanaro.schedule_hanaro.customer.service;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.customer.dto.RegisterReservationDto;
import com.hanaro.schedule_hanaro.global.domain.Section;
import com.hanaro.schedule_hanaro.global.repository.CsVisitRepository;
import com.hanaro.schedule_hanaro.global.domain.CsVisit;
import com.hanaro.schedule_hanaro.global.repository.SectionRepository;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class CsVisitService {
	private final CsVisitRepository csVisitRepository;
	private final SectionRepository sectionRepository;
	private final SectionService sectionService;
	private final EntityManager entityManager;

	public CsVisitService(CsVisitRepository csVisitRepository, SectionRepository sectionRepository,
		SectionService sectionService, EntityManager entityManager) {
		this.csVisitRepository = csVisitRepository;
		this.sectionRepository = sectionRepository;
		this.sectionService = sectionService;
		this.entityManager = entityManager;
	}

	// 트랜잭션 단위를 최소화
	// public int increase(RegisterReservationDto registerReservationDto) throws InterruptedException {
	// 	int totalNum = -1;
	// 	while (true) {
	// 		try {
	// 			// Tracational Annotation이 오류가 발생하는 가장 작은 단위로 들어가야함!
	// 			totalNum = increaseWait(registerReservationDto);
	// 			break;
	// 		} catch (OptimisticLockingFailureException ex) {
	// 			String threadName = Thread.currentThread().getName();
	// 			System.out.println(threadName + " : " + ex.getMessage());
	// 			Thread.sleep(500);
	// 		}
	// 	}
	//
	// 	return totalNum;
	// }

	@Transactional // 트랜잭션 단위 분리
	public int increaseWait(RegisterReservationDto registerReservationDto) {
		CsVisit optimisticLock = csVisitRepository.findById(registerReservationDto.csVisitId()).orElseThrow();
		optimisticLock.increase();
		csVisitRepository.saveAndFlush(optimisticLock);

		return optimisticLock.getTotalNum();
	}
}
