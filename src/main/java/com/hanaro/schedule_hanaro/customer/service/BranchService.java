package com.hanaro.schedule_hanaro.customer.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.customer.dto.request.BranchListCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchDetailResponse;
import com.hanaro.schedule_hanaro.customer.repository.CsVisitRepository;
import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.customer.repository.BranchRepository;
import com.hanaro.schedule_hanaro.global.domain.CsVisit;
import com.hanaro.schedule_hanaro.global.domain.enums.BranchType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchService {
	private final BranchRepository branchRepository;
	private final CsVisitRepository csVisitRepository;

	public BranchDetailResponse findBranchById(Long id){
		Branch branch = branchRepository.findById(id).orElseThrow();
		System.out.println(LocalDate.now());
		CsVisit csVisit = csVisitRepository.findCsVisitByBranchIdAndDate(id, LocalDate.now());
		System.out.println(csVisit);
		return BranchDetailResponse.of(branch.getId(), branch.getName(), branch.getAddress(), branch.getTel(),
			branch.getBusinessTime(), branch.getBranchType().toString(), csVisit.getCurrentNum(), csVisit.getTotalNum(),
			csVisit.getWaitAmount());
	}

	public String saveBranchList(BranchListCreateRequest branchList){
		branchList.branches()
			.forEach(branchDto -> branchRepository.save(
				Branch.builder()
					// .branchNum(branchDto.id())
					.name(branchDto.name())
					.branchType(BranchType.valueOf(branchDto.type()))
					.xPosition(branchDto.position_x())
					.yPosition(branchDto.position_y())
					.address(branchDto.address())
					.tel(branchDto.tel())
					.businessTime(branchDto.business_hours())
					.build()));

		return "Success";
	}
}
