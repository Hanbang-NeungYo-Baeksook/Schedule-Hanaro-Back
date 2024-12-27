package com.hanaro.schedule_hanaro.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	// 400
	MISSING_REQUEST_PARAMETER(40001, HttpStatus.BAD_REQUEST, "파라미터가 존재하지 않습니다."),
	WRONG_REQUEST_PARAMETER(40002, HttpStatus.BAD_REQUEST, "잘못된 파라미터입니다."),
	FULL_CALL_RESERVATION(40003, HttpStatus.BAD_REQUEST, "예약이 가득찬 시간대입니다."),
	WRONG_CALL_STATUS(40004, HttpStatus.BAD_REQUEST, "잘못된 전화 상담 상태입니다."),
	WRONG_INQUIRY_STATUS(40005, HttpStatus.BAD_REQUEST, "잘못된 1:1 문의 상태입니다."),
	ALREADY_RESERVED(40006, HttpStatus.BAD_REQUEST, "이미 예약되었습니다."),
	VISIT_LIMIT_OVER(40007, HttpStatus.BAD_REQUEST, "중복예약 횟수 초과"),
	BRANCH_CLOSED(40008, HttpStatus.BAD_REQUEST, "영업시간이 아닙니다."),
	ALREADY_PROGRESS(40009, HttpStatus.BAD_REQUEST, "해당 방문은 이미 진행 중입니다."),
	INVALID_VISIT_NUMBER(40010, HttpStatus.BAD_REQUEST, "잘못된 방문 번호입니다."),
	INVALID_TOTAL_VISITOR_COUNT(40011, HttpStatus.BAD_REQUEST, "잘못된 방문자 수입니다."),
	VISIT_TIME_EXPIRED(40012, HttpStatus.BAD_REQUEST, "방문 시간이 만료되었습니다."),
	ALREADY_COMPLETE(40013, HttpStatus.BAD_REQUEST, "이미 완료된 상담입니다."),
	INVALID_CATEGORY(40014, HttpStatus.BAD_REQUEST, "잘못된 카테고리 값입니다."),
	METHOD_MISMATCH(40015, HttpStatus.BAD_REQUEST, "메소드 타입이 올바르지 않습니다."),
	ALREADY_POST_MEMO(40016, HttpStatus.BAD_REQUEST, "이미 메모가 등록된 상담입니다."),
	ALREADY_POST_RESPONSE(40017, HttpStatus.BAD_REQUEST, "이미 답변이 등록된 상담입니다."),
	EMPTY_WAITS(40018, HttpStatus.BAD_REQUEST, "대기 중인 상담이 더이상 없습니다."),
	ALREADY_PROGRESS_COUNSELATION(40019, HttpStatus.BAD_REQUEST, "이미 진행 중인 상담이 있습니다."),

	// 401
	EXPIRED_ACCESS_TOKEN(40101, HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
	UNSUPPORTED_TOKEN(40102, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
	MALFORMED_ACCESS_TOKEN(40103, HttpStatus.BAD_REQUEST, "토큰이 올바르지 않습니다."),
	CANNOT_AUTHORIZED(40104, HttpStatus.UNAUTHORIZED, "인증을 진행할 수 없습니다."),

	// 403
	FORBIDDEN_REQUEST(40300, HttpStatus.FORBIDDEN, "권한이 존재하지 않습니다."),
	NOT_FOUND_REFRESH_TOKEN(40301, HttpStatus.FORBIDDEN, "refresh 토큰을 찾을 수 없습니다."),
	NOT_MATCHED_REFRESH_TOKEN(40302, HttpStatus.FORBIDDEN, "refresh 토큰이 일치하지 않습니다."),

	// 404
	NOT_FOUND_CUSTOMER(40401, HttpStatus.NOT_FOUND, "존재하지 않는 고객입니다."),
	NOT_FOUND_ADMIN(40402, HttpStatus.NOT_FOUND, "존재하지 않는 관리자입니다."),
	NOT_FOUND_BRANCH(40403, HttpStatus.NOT_FOUND, "존재하지 않는 영업점입니다."),
	NOT_FOUND_VISIT(40404, HttpStatus.NOT_FOUND, "존재하지 않는 방문 정보입니다."),
	NOT_FOUND_CALL(40405, HttpStatus.NOT_FOUND, "존재하지 않는 전화 상담입니다."),
	NOT_FOUND_INQUIRY(40406, HttpStatus.NOT_FOUND, "존재하지 않는 1:1 문의입니다."),
	NOT_FOUND_CALL_MEMO(40407, HttpStatus.NOT_FOUND, "존재하지 않는 전화 메모입니다."),
	NOT_FOUND_INQUIRY_RESPONSE(40408, HttpStatus.NOT_FOUND, "존재하지 않는 1:1 답변입니다."),
	NOT_FOUND_SECTION(40409, HttpStatus.NOT_FOUND, "해당 카테고리의 섹션 데이터를 찾을 수 없습니다."),
	NOT_FOUND_CS_VISIT(40410, HttpStatus.NOT_FOUND, "해당 지점의 방문 통계 데이터를 찾을 수 없습니다."),
	NOT_FOUND_NEXT_VISITOR(40411, HttpStatus.NOT_FOUND, "다음 대기 방문자가 존재하지 않습니다."),
	NOT_FOUND_DATA(40499, HttpStatus.NOT_FOUND, "존재하지 않는 데이터입니다."),

	// 409
	CONCURRENT_VISIT_UPDATE(40900, HttpStatus.CONFLICT, "방문 정보가 동시에 수정되었습니다."),
	CONFLICTING_CALL_RESERVATION(40013, HttpStatus.CONFLICT, "해당 시간대에 이미 예약이 존재합니다.");

	private final Integer code;
	private final HttpStatus httpStatus;
	private final String message;
}
