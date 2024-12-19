package com.hanaro.schedule_hanaro.customer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.schedule_hanaro.customer.dto.RegisterReservationDto;
import com.hanaro.schedule_hanaro.global.domain.Section;
import com.hanaro.schedule_hanaro.global.repository.SectionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SectionService {
	private final SectionRepository sectionRepository;

	@Transactional
	public void increase(RegisterReservationDto registerReservationDto) {
		Section section = sectionRepository.findByIdWithOptimisticLock(registerReservationDto.sectionId()).orElseThrow();
		section.increase(registerReservationDto.waitTime());
		sectionRepository.saveAndFlush(section);
	}
}
