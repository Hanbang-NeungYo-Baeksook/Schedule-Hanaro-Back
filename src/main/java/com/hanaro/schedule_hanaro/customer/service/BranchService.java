package com.hanaro.schedule_hanaro.customer.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.customer.dto.request.BranchListCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.AllBranchResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchDetailResponse;
import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.customer.repository.BranchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchService {
	private final BranchRepository branchRepository;

	public BranchDetailResponse findByBranchNum(long branchNum){
		Branch branch = branchRepository.findBranchByBranchNum(branchNum);
		return BranchDetailResponse.from(branch);
	}

	public AllBranchResponse findAllBranch(){
		List<Branch> branchList = branchRepository.findAll();
		return AllBranchResponse.from(branchList);
	}

	public String saveBranchList(BranchListCreateRequest branchList){
		branchList.branches()
			.forEach(branchDto -> branchRepository.save(
				Branch.builder()
					.branchNum(branchDto.id())
					.name(branchDto.name())
					.type(branchDto.type())
					.xPosition(branchDto.position_x())
					.yPosition(branchDto.position_y())
					.address(branchDto.address())
					.tel(branchDto.tel())
					.businessTime(branchDto.business_hours())
					.build()));
		return "Success";
	}
}
