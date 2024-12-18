package com.hanaro.schedule_hanaro.customer.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.hanaro.schedule_hanaro.customer.dto.request.VisitCreateRequest;
import com.hanaro.schedule_hanaro.customer.dto.response.VisitDetailResponse;
import com.hanaro.schedule_hanaro.customer.dto.response.VisitListResponse;
import com.hanaro.schedule_hanaro.global.domain.Section;
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

	public VisitService(VisitRepository visitRepository, BranchRepository branchRepository,
		CustomerRepository customerRepository, CsVisitRepository csVisitRepository,
		SectionRepository sectionRepository) {
		this.visitRepository = visitRepository;
		this.branchRepository = branchRepository;
		this.customerRepository = customerRepository;
		this.csVisitRepository = csVisitRepository;
		this.sectionRepository = sectionRepository;
	}

	@Transactional
	public Long addVisitReservation(
		VisitCreateRequest visitReservationCreateRequest
	) throws RuntimeException, InterruptedException {
		Customer customer = customerRepository.findById(
			visitReservationCreateRequest.customerId()
		).orElseThrow();
		Branch branch = branchRepository.findById(
			visitReservationCreateRequest.branchId()
		).orElseThrow();
		Section section = sectionRepository.findByBranchAndSectionType(branch,
			visitReservationCreateRequest.sectionType()).orElseThrow();
		LocalDateTime now = LocalDateTime.now();

		if (isReserved(customer, section, now)) {
			throw new RuntimeException("Branch Reserved");
		}

		if (isLimitOver(customer, now)) {
			throw new RuntimeException("Limit Over");
		}

		if (isClosed(branch, now)) {
			throw new RuntimeException("Branch Closed");
		}

		String content = visitReservationCreateRequest.content();

		String tags = getTags(content);

		Long csVisitId = csVisitRepository.findByBranchIdAndDate(
				branch.getId(),
				now.toLocalDate()
			)
			.orElseThrow()
			.getId();
		System.out.println("csVisitId = " + csVisitId);

		while (true) {
			try {
				System.out.println("Try Lock");
				CsVisit optimisticLock = csVisitRepository.findByWithOptimisticLock(csVisitId).orElseThrow();
				optimisticLock.increase();
				int totalNum = optimisticLock.getTotalNum();
				csVisitRepository.saveAndFlush(optimisticLock);

				Visit savedVisit = visitRepository.save(
					Visit.builder()
						.customer(customer)
						.section(section)
						.visitDate(now.toLocalDate())
						.num(totalNum)
						.content(content)
						.tags(tags)
						.build()
				);
				System.out.println("savedVisit.getId() = " + savedVisit.getId());
				return savedVisit.getId();
			} catch (OptimisticLockException ex) {
				System.out.println("OptimisticLockException, Sleep");
				Thread.sleep(50);
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

	private boolean isLimitOver(Customer customer, LocalDateTime now) {
		int count = visitRepository.countByCustomerAndVisitDateAndStatus(
			customer, now.toLocalDate(), Status.PENDING
		);
		return count >= LIMIT;
	}

	private static boolean isClosed(Branch branch, LocalDateTime now) {
		String[] businessTime = branch.getBusinessTime().split("~");
		String startTime = businessTime[0];
		String endTime = businessTime[1];
		String curTime = now.toLocalTime().toString();
		return curTime.compareTo(startTime) < 0 || curTime.compareTo(endTime) > 0;
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
			.getCurrentNum();

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
}
