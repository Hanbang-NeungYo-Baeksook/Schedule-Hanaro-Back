package com.hanaro.schedule_hanaro.branch.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.branch.dto.request.BranchListCreateRequest;
import com.hanaro.schedule_hanaro.branch.dto.response.AllBranchResponse;
import com.hanaro.schedule_hanaro.branch.dto.response.BranchDetailResponse;
import com.hanaro.schedule_hanaro.branch.entity.Branch;
import com.hanaro.schedule_hanaro.branch.repository.BranchRepository;

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
				Branch.of(branchDto.id(), branchDto.name(), branchDto.type(), branchDto.position_x(),
					branchDto.position_y(), branchDto.address(), branchDto.tel(), branchDto.business_hours())));
		return "Success";
	}
}