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
	WRONG_INQUIRY_STATUS(40003, HttpStatus.BAD_REQUEST, "잘못된 1:1 문의 상태입니다."),

	// 403
	FORBIDDEN_REQUEST(40300, HttpStatus.FORBIDDEN, "권한이 존재하지 않습니다."),

	// 404
	NOT_FOUND_CUSTOMER(40401, HttpStatus.NOT_FOUND, "존재하지 않는 고객입니다."),
	NOT_FOUND_ADMIN(40402, HttpStatus.NOT_FOUND, "존재하지 않는 관리자입니다."),
	NOT_FOUND_BRANCH(40403, HttpStatus.NOT_FOUND, "존재하지 않는 영업점입니다."),
	NOT_FOUND_VISIT(40404, HttpStatus.NOT_FOUND, "존재하지 않는 방문 정보입니다."),
	NOT_FOUND_CALL(40405, HttpStatus.NOT_FOUND, "존재하지 않는 전화 상담입니다."),
	NOT_FOUND_INQUIRY(40406, HttpStatus.NOT_FOUND, "존재하지 않는 1:1 문의입니다."),
	NOT_FOUND_CALL_MEMO(40407, HttpStatus.NOT_FOUND, "존재하지 않는 전화 메모입니다."),
	NOT_FOUND_INQUIRY_RESPONSE(40408, HttpStatus.NOT_FOUND, "존재하지 않는 1:1 답변입니다."),
	NOT_FOUND_DATA(40499, HttpStatus.NOT_FOUND, "존재하지 않는 데이터입니다."),
	;
	private final Integer code;
	private final HttpStatus httpStatus;
	private final String message;
}
