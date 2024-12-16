// package com.hanaro.schedule_hanaro.global.auth.service;
//
//
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.stereotype.Service;
//
// import com.hanaro.schedule_hanaro.admin.repository.AdminRepository;
// import com.hanaro.schedule_hanaro.customer.repository.BranchRepository;
// import com.hanaro.schedule_hanaro.global.auth.dto.request.AuthAdminSignUpRequest;
// import com.hanaro.schedule_hanaro.global.auth.dto.request.AuthSignUpRequest;
// import com.hanaro.schedule_hanaro.global.domain.Admin;
// import com.hanaro.schedule_hanaro.global.domain.Branch;
// import com.hanaro.schedule_hanaro.global.domain.Customer;
// import com.hanaro.schedule_hanaro.global.domain.enums.Gender;
// import com.hanaro.schedule_hanaro.customer.repository.CustomerRepository;
//
// import lombok.RequiredArgsConstructor;
//
// @Service
// @RequiredArgsConstructor
// public class AuthService {
// 	private final CustomerRepository customerRepository;
// 	private final AdminRepository adminRepository;
// 	private final BranchRepository branchRepository;
// 	private final BCryptPasswordEncoder bCryptPasswordEncoder;
//
// 	public void signUp(AuthSignUpRequest authSignUpRequest){
// 		customerRepository.save(Customer.builder()
// 			.authId(authSignUpRequest.authId())
// 			.password(bCryptPasswordEncoder.encode(authSignUpRequest.password()))
// 			.name(authSignUpRequest.name())
// 			.phoneNum(authSignUpRequest.phoneNum())
// 			.birth(authSignUpRequest.birth())
// 			.gender(Gender.valueOf(authSignUpRequest.gender()))
// 			.build());
// 	}
//
// 	public String adminSignUpAdmin(AuthAdminSignUpRequest authAdminSignUpRequest) {
// 		Branch branch = branchRepository.findById(1L).orElseThrow();
// 		adminRepository.save(Admin.builder()
// 				.authId(authAdminSignUpRequest.authId())
// 				.password(bCryptPasswordEncoder.encode(authAdminSignUpRequest.password()))
// 				.name(authAdminSignUpRequest.name())
// 				.branch(branch)
// 			.build()
// 		);
//
// 		return "Success";
// 	}
// }
