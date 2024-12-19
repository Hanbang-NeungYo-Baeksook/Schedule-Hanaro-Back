package com.hanaro.schedule_hanaro.customer.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.customer.dto.CancelReservationDto;
import com.hanaro.schedule_hanaro.customer.dto.RegisterReservationDto;
import com.hanaro.schedule_hanaro.customer.dto.request.VisitCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.VisitDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.VisitListResponse;
import com.hanaro.schedule_hanaro.global.domain.Section;
import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import com.hanaro.schedule_hanaro.global.domain.enums.SectionType;
import com.hanaro.schedule_hanaro.global.exception.ErrorCode;
import com.hanaro.schedule_hanaro.global.exception.GlobalException;
import com.hanaro.schedule_hanaro.global.repository.BranchRepository;
import com.hanaro.schedule_hanaro.global.repository.CsVisitRepository;
import com.hanaro.schedule_hanaro.global.repository.CustomerRepository;
import com.hanaro.schedule_hanaro.global.repository.SectionRepository;
import com.hanaro.schedule_hanaro.global.repository.VisitRepository;
import com.hanaro.schedule_hanaro.global.domain.Branch;
import com.hanaro.schedule_hanaro.global.domain.CsVisit;
import com.hanaro.schedule_hanaro.global.domain.Customer;
import com.hanaro.schedule_hanaro.global.domain.Visit;
import com.hanaro.schedule_hanaro.global.domain.enums.Status;
import com.hanaro.schedule_hanaro.global.utils.PrincipalUtils;

import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;

// 여긴 수정 주루룩 해야함!
@Service
public class VisitService {
	private final int LIMIT = 3;

	private final VisitRepository visitRepository;

	private final CustomerRepository customerRepository;

	private final BranchRepository branchRepository;

	private final CsVisitRepository csVisitRepository;

	private final SectionRepository sectionRepository;

	private final CsVisitService csVisitService;
	private final SectionService sectionService;

	public VisitService(VisitRepository visitRepository, BranchRepository branchRepository,
		CustomerRepository customerRepository, CsVisitRepository csVisitRepository,
		SectionRepository sectionRepository, CsVisitService csVisitService, SectionService sectionService) {
		this.visitRepository = visitRepository;
		this.branchRepository = branchRepository;
		this.customerRepository = customerRepository;
		this.csVisitRepository = csVisitRepository;
		this.sectionRepository = sectionRepository;
		this.csVisitService = csVisitService;
		this.sectionService = sectionService;
	}

	public Long addVisitReservation(
		VisitCreateRequest visitReservationCreateRequest,
		Authentication authentication
	) throws RuntimeException, InterruptedException {
		Customer customer = customerRepository.findById(PrincipalUtils.getId(authentication))
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_CUSTOMER));

		Branch branch = branchRepository.findById(
				visitReservationCreateRequest.branchId())
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_BRANCH));

		Section section = sectionRepository.findByBranchAndSectionType(branch,
				getSectionByCategory(visitReservationCreateRequest.category()))
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_DATA));

		LocalDateTime now = LocalDateTime.now();

		if (isReserved(customer, section, now)) {
			throw new GlobalException(ErrorCode.ALREADY_RESERVED);
		}
		isLimitOver(customer, now);
		isClosed(branch, now);

		String content = visitReservationCreateRequest.content();
		String tags = getTags(content);

		Long csVisitId = csVisitRepository.findByBranchIdAndDate(
				branch.getId(),
				now.toLocalDate()
			)
			.orElseThrow().getId();

		int totalNum;
		while (true) {
			try {
				RegisterReservationDto registerReservationDto = RegisterReservationDto.of(csVisitId, section.getId(),
					visitReservationCreateRequest.category().getWaitTime());
				totalNum = csVisitService.increaseWait(registerReservationDto);
				sectionService.increaseWait(registerReservationDto);
				break;
			} catch (OptimisticLockingFailureException ex) {
				String threadName = Thread.currentThread().getName();
				System.out.println(threadName + " : " + ex.getMessage());
				Thread.sleep(500);
			}
		}
		System.out.println(Thread.currentThread().getName() + " : totalNum = " + totalNum);

		Visit savedVisit = visitRepository.save(
			Visit.builder()
				.customer(customer)
				.section(section)
				.visitDate(now.toLocalDate())
				.num(totalNum)
				.content(content)
				.tags(tags)
				.category(visitReservationCreateRequest.category())
				.build()
		);
		return savedVisit.getId();
	}

	// TODO: 타입 정의 하고 마무리
	private SectionType getSectionByCategory(Category category) {
		switch (category) {
			case FUND,DEPOSIT -> {
				return SectionType.TEMP1;
			}
			case FOREX -> {
				return SectionType.TEMP2;
			}
			default -> {
				return SectionType.TEMP3;
			}
		}
	}

	private boolean isReserved(Customer customer, Section section, LocalDateTime now) {
		if (visitRepository.existsByCustomerAndSectionAndVisitDateAndStatus(
			customer, section, now.toLocalDate(), Status.PENDING)
		) {
			return true;
		}
		return false;
	}

	private void isLimitOver(Customer customer, LocalDateTime now) {
		int count = visitRepository.countByCustomerAndVisitDateAndStatus(
			customer, now.toLocalDate(), Status.PENDING
		);
		if (count >= LIMIT) {
			throw new GlobalException(ErrorCode.VISIT_LIMIT_OVER);
		}
	}

	private void isClosed(Branch branch, LocalDateTime now) {
		String[] businessTime = branch.getBusinessTime().split("~");
		String startTime = businessTime[0];
		String endTime = businessTime[1];
		String curTime = now.toLocalTime().toString();
		if (curTime.compareTo(startTime) < 0 || curTime.compareTo(endTime) > 0) {
			throw new GlobalException(ErrorCode.BRANCH_CLOSED);
		}
	}

	private String getTags(String content) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 50; i++) {
			builder.append(Math.random() > 0.5 ? '0' : '1');
		}
		return builder.toString();
	}

	public VisitDetailResponse getVisitDetail(Long visitId) {
		Visit visit = visitRepository.findById(visitId).orElseThrow();

		int currentNum = csVisitRepository.findByBranchIdAndDate(
				visit.getSection().getBranch().getId(),
				LocalDate.now()
			)
			.orElseThrow()
			.getTotalNum();

		// TODO: * Calculate Waiting Time *
		// TODO: 1. Find All Visit with BranchId Less than Num And Status
		List<Visit> visits = visitRepository.findAllBySectionIdAndNumLessThanAndStatus(
			visit.getSection().getId(), visit.getNum(), Status.PENDING
		);
		// TODO: 2. Calculate Waiting Amount
		int waitingAmount = visits.size();
		// TODO: 3. Calculate Waiting Time
		int waitingTime = calculateWaitingTime(visits);

		return VisitDetailResponse.of(
			visit.getId(),
			visit.getSection().getBranch().getName(),
			visit.getNum(),
			currentNum,
			waitingAmount,
			waitingTime
		);
	}

	private int calculateWaitingTime(List<Visit> visits) {
		return visits.size() * 5;
	}

	public VisitListResponse getVisitList(Authentication authentication, int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		Long customerId = PrincipalUtils.getId(authentication);

		Slice<Visit> visitSlice = visitRepository.findByCustomerIdAndStatus(customerId, Status.PENDING, pageable);

		List<VisitListResponse.VisitData> visitDataList = visitSlice.getContent().stream()
			.map(visit -> {
				CsVisit csVisit = csVisitRepository.findByBranchIdAndDate(
					visit.getSection().getBranch().getId(), LocalDate.now()
				).orElseThrow();
				List<Visit> visits = visitRepository.findAllBySection_Id(
					visit.getSection().getId()
				);
				return VisitListResponse.VisitData.builder()
					.visitId(visit.getId())
					.visitNum(visit.getNum())
					.branchName(visit.getSection().getBranch().getName())
					.waitingAmount(csVisit.getWaitAmount())
					.waitingTime(calculateWaitingTime(visits))
					.build();
			})
			.toList();

		VisitListResponse.Pagination pagination = VisitListResponse.Pagination.builder()
			.currentPage(page)
			.pageSize(size)
			.hasNext(visitSlice.hasNext())
			.build();

		return VisitListResponse.builder()
			.data(visitDataList)
			.pagination(pagination)
			.build();
	}

	public String deleteVisitReservation(Long visitId) throws InterruptedException {
		Visit visit = visitRepository.findById(visitId)
			.orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND_VISIT));
		if (visit.getStatus().equals(Status.COMPLETE)) {
			throw new GlobalException(ErrorCode.ALREADY_RESERVED);
		}
		while (true) {
			try {
				// 창구에 대기 현황 반영
				sectionService.decreaseWait(
					CancelReservationDto.of(visit.getSection().getId(), visit.getCategory().getWaitTime()));
				break;
			} catch (OptimisticLockingFailureException ex) {
				String threadName = Thread.currentThread().getName();
				System.out.println(threadName + " : " + ex.getMessage());
				Thread.sleep(500);
			}
		}
		return "Success";
	}
}
