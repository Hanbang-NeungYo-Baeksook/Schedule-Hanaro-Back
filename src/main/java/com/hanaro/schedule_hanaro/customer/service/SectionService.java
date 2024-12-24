package com.hanaro.schedule_hanaro.customer.service;

import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.schedule_hanaro.customer.dto.CancelReservationDto;
import com.hanaro.schedule_hanaro.customer.dto.RegisterReservationDto;
import com.hanaro.schedule_hanaro.global.domain.Section;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.SectionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SectionService {
	private final SectionRepository sectionRepository;

	@Transactional
	public void increaseWait(RegisterReservationDto registerReservationDto) {
		Section section = sectionRepository.findByIdWithOptimisticLock(registerReservationDto.sectionId()).orElseThrow();
		section.increase(registerReservationDto.waitTime());
		sectionRepository.saveAndFlush(section);
	}

	// 창구 대기 인원 -1 && 대기 시간 -amount
	@Transactional
	public void decreaseWait(CancelReservationDto cancelReservationDto) {
		Section section = sectionRepository.findByIdWithOptimisticLock(cancelReservationDto.sectionId())
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_DATA));
		section.decrease(cancelReservationDto.waitTime());
		sectionRepository.saveAndFlush(section);
	}

	public
}
