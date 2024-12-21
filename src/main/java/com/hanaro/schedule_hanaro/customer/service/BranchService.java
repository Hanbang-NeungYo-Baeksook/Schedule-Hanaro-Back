package com.hanaro.schedule_hanaro.customer.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.schedule_hanaro.customer.vo.BankVO;
import com.hanaro.schedule_hanaro.customer.dto.request.BranchListCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.AtmInfoDto;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchListResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchRecommendationResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchWithMetrics;
import com.hanaro.schedule_hanaro.global.domain.Section;
import com.hanaro.schedule_hanaro.global.domain.enums.SectionType;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.BranchRepository;
import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.enums.BranchType;
import com.hanaro.schedule_hanaro.global.repository.SectionRepository;
import com.hanaro.schedule_hanaro.global.utils.DistanceUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchService {
	private final BranchRepository branchRepository;
	private final SectionRepository sectionRepository;

	public BranchDetailResponse findBranchById(Long branchId){
		Map<Long, BranchDetailResponse>dtoMap=new LinkedHashMap<>();
		List<BankVO> branches= branchRepository.findBranchByBranch_Id(branchId);

		branches.forEach(objects -> {

			dtoMap.computeIfAbsent(objects.branchId(), id -> new BranchDetailResponse(
				objects.branchId(), objects.name(), objects.xPosition(), objects.yPosition(), objects.address(),
				objects.tel(), objects.branchType().getBranchType(), new ArrayList<>(), new ArrayList<>(),
				new ArrayList<>(), 0
			));

				BranchDetailResponse dto = dtoMap.get(objects.branchId());
				dto.sectionTypes().add(objects.sectionType().getType());
				dto.waitAmount().add(objects.waitAmount());
				dto.waitTime().add(objects.waitTime());
			}
		);

		return dtoMap.get(branchId);
	}

	public BranchListResponse listBranch(double userLat, double userLon) {

		Map<Long, BranchDetailResponse>dtoMap=new LinkedHashMap<>();
		List<Branch> atmList = branchRepository.findAllByBranchTypeOrderByIdAsc(BranchType.ATM);
		List<BankVO> result = branchRepository.findBranchByBranchType(BranchType.BANK);

		result.forEach(objects -> {

			dtoMap.computeIfAbsent(objects.branchId(), id -> new BranchDetailResponse(
				objects.branchId(), objects.name(), objects.xPosition(), objects.yPosition(), objects.address(),
				objects.tel(), objects.branchType().getBranchType(), new ArrayList<>(), new ArrayList<>(),
				new ArrayList<>(),
				Math.round(DistanceUtils.calculateDistance(userLat, userLon, Double.parseDouble(objects.yPosition()),
					Double.parseDouble(objects.xPosition())) * 1000)
			));

			BranchDetailResponse dto = dtoMap.get(objects.branchId());
			dto.sectionTypes().add(objects.sectionType().getType());
			dto.waitAmount().add(objects.waitAmount());
			dto.waitTime().add(objects.waitTime());
			}
		);

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

		return BranchListResponse.of(new ArrayList<>(dtoMap.values()), atmInfoDtoList);
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
				double distance = DistanceUtils.calculateDistance(
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
					section.getCurrentNum()
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
