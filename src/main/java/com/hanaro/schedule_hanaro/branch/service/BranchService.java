package com.hanaro.schedule_hanaro.branch.service;

import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.branch.dto.response.BranchResponse;
import com.hanaro.schedule_hanaro.branch.entity.Branch;
import com.hanaro.schedule_hanaro.branch.repository.BranchRepository;

@Service
public class BranchService {
	private BranchRepository branchRepository;

	public BranchResponse findByBranchId(long branchId){
		Branch branch = branchRepository.findById(branchId);
		return BranchResponse.from(branch.getBranchNum(), branch.getName(), branch.getType(), branch.getXPosition(),
			branch.getYPosition(), branch.getAddress(), branch.getTel(), branch.getBusinessTime());
	}
}
