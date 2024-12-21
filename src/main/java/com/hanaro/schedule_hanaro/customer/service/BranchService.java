package com.hanaro.schedule_hanaro.customer.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.schedule_hanaro.customer.dto.request.BranchListCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.AtmInfoDto;
import com.hanaro.schedule_hanaro.customer.dto.response.BankInfoDto;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchListResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchRecommendationResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchWithMetrics;
import com.hanaro.schedule_hanaro.global.domain.Section;
import com.hanaro.schedule_hanaro.global.domain.enums.SectionType;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.BranchRepository;
import com.hanaro.schedule_hanaro.global.repository.CsVisitRepository;
import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.CsVisit;
import com.hanaro.schedule_hanaro.global.domain.enums.BranchType;
import com.hanaro.schedule_hanaro.global.repository.SectionRepository;
import com.hanaro.schedule_hanaro.global.utils.DistanceUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchService {
	private final BranchRepository branchRepository;
	private final CsVisitRepository csVisitRepository;
	private final SectionRepository sectionRepository;
	private final DistanceUtils distanceUtils;

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

	// 추천 영업점 알고리즘
	public List<BranchRecommendationResponse> recommendBranches(double userLat, double userLon,
		String transportType, SectionType sectionType) {
		// 최대 거리 설정
		double maxDistance = transportType.equals("도보") ? 3.0 : 15.0;

		// 가중치 설정
		double distanceWeight = transportType.equals("도보") ? 0.7 : 0.6;
		double categoryWeight = 1.0 - distanceWeight;

		// BranchType이 "BANK"인 데이터만 가져오기
		List<Branch> branches = branchRepository.findAllByBranchType(BranchType.BANK);

		// Branch 데이터를 처리하여 BranchWithMetrics 리스트 생성
		List<BranchWithMetrics> branchMetrics = branches.stream()
			.map(branch -> {
				// 거리 계산
				double distance = distanceUtils.calculateDistance(
					userLat, userLon,
					Double.parseDouble(branch.getYPosition()), Double.parseDouble(branch.getXPosition())
				);

				// Section 데이터 가져오기
				Section section = sectionRepository.findByBranchAndSectionType(branch, sectionType)
					.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_SECTION));

				int currentNum = section.getCurrentNum();
				int waitAmount = section.getWaitAmount();

				return BranchWithMetrics.of(branch, distance, waitAmount);
			})
			.filter(bwm -> bwm.distance() <= maxDistance) // 최대 거리 제한 필터링
			.sorted(Comparator.comparingDouble(BranchWithMetrics::distance)) // 거리 기준 정렬
			.limit(9) // 최대 9개로 제한
			.toList();

		// 3개씩 그룹화하여 각 그룹의 최적 영업점 선택
		List<BranchWithMetrics> bestBranches = IntStream.range(0, branchMetrics.size())
			.boxed()
			.collect(Collectors.groupingBy(index -> index / 3)) // 3개씩 그룹화
			.values()
			.stream()
			.map(group -> group.stream()
				.map(branchMetrics::get) // 그룹 내 BranchWithMetrics 가져오기
				.min(Comparator.comparingDouble(
					bwm -> calculateWeight(bwm.distance(), bwm.waitAmount(), distanceWeight, categoryWeight)
				)) // 가중치가 가장 낮은 영업점 선택
				.orElseThrow(() -> new IllegalStateException("그룹 내에서 최적 영업점을 찾을 수 없습니다."))
			)
			.toList();

		// DTO 변환하여 결과 반환
		return bestBranches.stream()
			.map(bwm -> {
				Branch branch = bwm.branch();
				Section section = sectionRepository.findByBranchAndSectionType(branch, sectionType)
					.orElseThrow(() -> new IllegalStateException("해당 카테고리의 섹션 데이터가 없습니다."));
				return BranchRecommendationResponse.of(
					branch.getId(),
					branch.getName(),
					branch.getAddress(),
					bwm.distance(),
					section.getWaitTime(),
					section.getCurrentNum(),
					BankInfoDto.of(
						branch.getId(),
						branch.getName(),
						branch.getXPosition(),
						branch.getYPosition(),
						branch.getAddress(),
						branch.getBranchType().name(),
						section.getCurrentNum(), // 현재 대기 인원
						section.getWaitAmount() // 총 대기 인원
					)
				);
			})
			.collect(Collectors.toList());
	}

	/**
	 * 거리와 예상 대기 인원에 기반하여 가중치를 계산합니다.
	 */
	private double calculateWeight(double distance, int waitAmount, double distanceWeight, double categoryWeight) {
		return (distance * distanceWeight) + (waitAmount * categoryWeight);
	}
}
