package com.hanaro.schedule_hanaro.customer.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.schedule_hanaro.customer.dto.request.BranchListCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.AtmInfoDto;
import com.hanaro.schedule_hanaro.customer.dto.response.BankInfoDto;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchListResponse;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.BranchRepository;
import com.hanaro.schedule_hanaro.global.repository.CsVisitRepository;
import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.CsVisit;
import com.hanaro.schedule_hanaro.global.domain.enums.BranchType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchService {
	private final BranchRepository branchRepository;
	private final CsVisitRepository csVisitRepository;

	public BranchDetailResponse findBranchById(Long id){
		Branch branch = branchRepository.findById(id).orElseThrow(()->new GlobalException(ErrorCode.NOT_FOUND_BRANCH));
		System.out.println(LocalDate.now());
		CsVisit csVisit = csVisitRepository.findCsVisitByBranchIdAndDate(id, LocalDate.now())
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_DATA));
		System.out.println(csVisit);
		return BranchDetailResponse.of(branch.getId(), branch.getName(), branch.getAddress(), branch.getTel(),
			branch.getBusinessTime(), branch.getBranchType().toString(), csVisit.getTotalNum(), csVisit.getTotalNum(),
			csVisit.getWaitAmount());
	}

	public BranchListResponse listBranch() {

		List<CsVisit> csVisitList = csVisitRepository.findAllByDateOrderByBranchAsc(LocalDate.now());
		List<Branch> atmList = branchRepository.findAllByBranchTypeOrderByIdAsc(BranchType.ATM);

		List<BankInfoDto> bankInfoDtoList = csVisitList.stream()
			.map(csVisit -> BankInfoDto.of(
				csVisit.getBranch().getId(),
				csVisit.getBranch().getName(),
				csVisit.getBranch().getXPosition(),
				csVisit.getBranch().getYPosition(),
				csVisit.getBranch().getAddress(),
				csVisit.getBranch().getBranchType().toString(),
				csVisit.getTotalNum(),
				csVisit.getTotalNum()
			))
			.toList();

		List<AtmInfoDto> atmInfoDtoList = atmList.stream()
			.map(atm -> AtmInfoDto.of(
				atm.getId(),
				atm.getName(),
				atm.getXPosition(),
				atm.getYPosition(),
				atm.getAddress(),
				atm.getBusinessTime(),
				atm.getBranchType().toString()
			))
			.toList();

		return BranchListResponse.of(bankInfoDtoList, atmInfoDtoList);
	}

	@Transactional
	public String saveBranchList(BranchListCreateRequest branchList) {

		branchList.branches()
			.forEach(branchDto -> branchRepository.save(
				Branch.builder()
					// .branchNum(branchDto.id())
					.name(branchDto.name())
					.branchType(BranchType.valueOf(branchDto.type()))
					.xPosition(branchDto.position_x())
					.yPosition(branchDto.position_y())
					.address(branchDto.address())
					.tel(branchDto.tel().replace("-", ""))
					.businessTime(branchDto.business_hours())
					.build()));

		return "Success";
	}
}
