package com.hanaro.schedule_hanaro.customer.service;

import java.util.List;

import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.schedule_hanaro.customer.dto.CancelReservationDto;
import com.hanaro.schedule_hanaro.customer.dto.RegisterReservationDto;
import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.Section;
import com.hanaro.schedule_hanaro.global.domain.enums.BranchType;
import com.hanaro.schedule_hanaro.global.domain.enums.SectionType;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.BranchRepository;
import com.hanaro.schedule_hanaro.global.repository.SectionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SectionService {
	private final BranchRepository branchRepository;
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

	@Transactional
	public void insertSections() {
		List<Branch> branchList = branchRepository.findAllByBranchType(BranchType.BANK);
		for (Branch branch : branchList) {
			sectionRepository.save(Section.builder().branch(branch).sectionType(SectionType.DEPOSIT).build());
			sectionRepository.save(Section.builder().branch(branch).sectionType(SectionType.PERSONAL_LOAN).build());
			sectionRepository.save(Section.builder().branch(branch).sectionType(SectionType.OTHERS).build());
		}
	}
}
