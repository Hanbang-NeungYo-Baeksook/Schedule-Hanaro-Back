package com.hanaro.schedule_hanaro.customer.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanaro.schedule_hanaro.customer.dto.request.BranchListCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.AtmInfoDto;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchListResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchRecommendationData;
import com.hanaro.schedule_hanaro.customer.dto.response.BranchWithMetrics;
import com.hanaro.schedule_hanaro.customer.vo.BankVO;
import com.hanaro.schedule_hanaro.customer.vo.ReservedListVO;
import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.Section;
import com.hanaro.schedule_hanaro.global.domain.Visit;
import com.hanaro.schedule_hanaro.global.domain.enums.BranchType;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.SectionType;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;
import com.hanaro.schedule_hanaro.global.domain.enums.TransportType;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.BranchRepository;
import com.hanaro.schedule_hanaro.global.repository.SectionRepository;
import com.hanaro.schedule_hanaro.global.repository.VisitRepository;
import com.hanaro.schedule_hanaro.global.utils.DistanceUtils;
import com.hanaro.schedule_hanaro.global.utils.GetSectionByCategory;
import com.hanaro.schedule_hanaro.global.utils.PrincipalUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BranchService {
	private final BranchRepository branchRepository;
	private final SectionRepository sectionRepository;
	private final VisitRepository visitRepository;

	public BranchDetailResponse findBranchById(Long branchId, double xPosition, double yPosition,
		Authentication authentication) {
		List<BankVO> results = branchRepository.findBranchByBranch_Id(branchId);
		List<ReservedListVO> reservedList = getReservedList(authentication);

		Map<Long, BranchDetailResponse> dtoMap = createBranchDtoMapFromBankVoList(results, reservedList, yPosition,
			xPosition);
		System.out.println(dtoMap.get(branchId).visitId());

		return dtoMap.get(branchId);
	}

	private List<ReservedListVO> getReservedList(Authentication authentication) {
		List<Visit> visitOptional = visitRepository.findByCustomer_IdAndStatus(PrincipalUtils.getId(authentication),
			Status.PENDING);
		return visitOptional.stream()
			.map(visit -> ReservedListVO.of(visit.getId(), visit.getSection().getBranch().getId()))
			.toList();
	}

	private Map<Long, BranchDetailResponse> createBranchDtoMapFromBankVoList(List<BankVO> bankVoList,
		List<ReservedListVO> reservedList, double userLat, double userLon) {
		Map<Long, BranchDetailResponse> dtoMap = new LinkedHashMap<>();
		System.out.println("예약리스트" + reservedList);
		bankVoList.forEach(objects -> {
			dtoMap.computeIfAbsent(objects.branchId(), id -> {
				Long visitId = 0L;
				boolean isReserved = false;
				for (ReservedListVO reserve : reservedList) {
					if (reserve.branchId().equals(objects.branchId())) {
						isReserved = true;
						visitId = reserve.visitId();
						break;
					}
				}
				return new BranchDetailResponse(
					objects.branchId(), objects.name(), objects.xPosition(), objects.yPosition(), objects.address(),
					objects.tel(), objects.businessHours(), objects.branchType().getBranchType(),
					isReserved, visitId, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
					(int)(DistanceUtils.calculateDistance(Double.parseDouble(objects.yPosition()),
						Double.parseDouble(objects.xPosition()), userLat, userLon) * 1000)
				);
			});
			BranchDetailResponse dto = dtoMap.get(objects.branchId());
			dto.sectionTypes().add(objects.sectionType() != null ? objects.sectionType().getType() : "null");
			dto.waitAmount().add(objects.waitAmount());
			dto.waitTime().add(objects.waitTime());
		});
		return dtoMap;
	}

	public BranchListResponse listBranch(double userLat, double userLon, String key, String section,
		Authentication authentication) {

		List<Branch> atmList = branchRepository.findAllByBranchTypeOrderByIdAsc(BranchType.ATM);
		List<BankVO> results = branchRepository.findBranchByBranchType(BranchType.BANK);

		List<ReservedListVO> reservedList = getReservedList(authentication);

		Map<Long, BranchDetailResponse> dtoMap = createBranchDtoMapFromBankVoList(results, reservedList, userLat,
			userLon);
		List<BranchDetailResponse> bankList = new ArrayList<>(dtoMap.values());
		System.out.println(bankList.get(2).branchName());

		if (key.equals("distance")) {
			bankList.sort(Comparator.comparing(BranchDetailResponse::distance));
			System.out.println("거리정렬완료");
		} else if (key.equals("wait")) {
			System.out.println("시간 진입");
			SectionType sectionType = SectionType.valueOf(section);
			System.out.println("섹션 가져옴");
			bankList.sort(
				Comparator.comparing(branchDetailResponse -> branchDetailResponse.waitTime()
					.get(branchDetailResponse.sectionTypes().indexOf(sectionType.getType()))));
			System.out.println("정렬 완료");
		} else {
			throw new GlobalException(ErrorCode.WRONG_REQUEST_PARAMETER);
		}

		List<AtmInfoDto> atmInfoDtoList = new ArrayList<>(atmList.stream()
			.map(atm -> AtmInfoDto.of(
				atm.getId(),
				atm.getName(),
				atm.getXPosition(),
				atm.getYPosition(),
				atm.getAddress(),
				atm.getBusinessTime(),
				atm.getBranchType().toString(),
				(int)(DistanceUtils.calculateDistance(userLat, userLon, Double.parseDouble(atm.getYPosition()),
					Double.parseDouble(atm.getXPosition())) * 1000)
			))
			.toList());
		atmInfoDtoList.sort(Comparator.comparing(AtmInfoDto::distance));
		return BranchListResponse.of(bankList, atmInfoDtoList);
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

	public List<BranchRecommendationData> recommendBranches(double userLat, double userLon, TransportType transportType,
		SectionType sectionType) {
		// 최대 거리 설정
		double maxDistance = transportType == TransportType.WALK ? 3.0 : 15.0;

		// 가중치 설정
		double distanceWeight = transportType == TransportType.WALK ? 0.7 : 0.5; // 가중치 수정
		double categoryWeight = 1.0 - distanceWeight;

		// BranchType이 "BANK"인 데이터만 가져오기
		List<Branch> branches = branchRepository.findAllByBranchType(BranchType.BANK);

		// 가장 가까운 영업점을 계산하여 저장
		BranchWithMetrics closesBranch = branches.stream()
			.map(branch -> {
				double distance = DistanceUtils.calculateDistance(
					userLat, userLon,
					Double.parseDouble(branch.getYPosition()), Double.parseDouble(branch.getXPosition())
				);

				Section section = sectionRepository.findByBranchAndSectionType(branch, sectionType)
					.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_SECTION));

				int waitAmount = section.getWaitAmount() == 0 ? 1 : section.getWaitAmount();

				return BranchWithMetrics.of(branch, distance, waitAmount);
			})
			.min(Comparator.comparingDouble(BranchWithMetrics::distance)) // 가장 가까운 영업점 저장(거리제한)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_BRANCH));

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

				int waitAmount = section.getWaitAmount();

				return BranchWithMetrics.of(branch, distance, waitAmount);
			})
			.filter(bwm -> bwm.distance() <= maxDistance) // 최대 거리 제한 필터링
			.sorted(Comparator.comparingDouble(BranchWithMetrics::distance)) // 거리 기준 정렬
			.limit(9) // 최대 9개로 제한
			.toList();

		// 거리 제한 때문에 리스트가 비어있을 경우 가장 가까운 영업점 추천
		if (branchMetrics.isEmpty()) {
			// 가장 가까운 영업점을 DTO로 변환
			Branch branch = closesBranch.branch();
			Section section = sectionRepository.findByBranchAndSectionType(branch, sectionType)
				.orElseThrow(() -> new IllegalStateException("해당 카테고리의 섹션 데이터가 없습니다."));

			return List.of(BranchRecommendationData.of(
				branch.getId(),
				branch.getName(),
				branch.getAddress(),
				closesBranch.distance(),
				section.getWaitTime(),
				section.getCurrentNum()
			));
		}

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
				return BranchRecommendationData.of(
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

	private double calculateWeight(double distance, int waitAmount, double distanceWeight, double categoryWeight) {
		return (distance * distanceWeight) + (waitAmount * categoryWeight);
	}
}
