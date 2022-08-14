package com.musinsa.pointapiserver.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	//500 인터널 에러 별 메시지 정의
    NO_DATA(HttpStatus.INTERNAL_SERVER_ERROR,"조회된 {0} 이/가 없습니다."),
    NOT_FOUND_DATA(HttpStatus.INTERNAL_SERVER_ERROR,"해당 {0} 이/가 존재하지 않습니다."),
    NO_POINT(HttpStatus.INTERNAL_SERVER_ERROR,"{0} 포인트가 부족합니다. 포인트 충전 후 다시 이용해주세요"),
    TRY_AGAIN_LATER(HttpStatus.INTERNAL_SERVER_ERROR,"앗! 잠시 예기치 못한 오류가 생겼습니다. 잠시후 다시 이용해주세요."),
    DUPLICATE_MEMBER_NO(HttpStatus.INTERNAL_SERVER_ERROR,"{0} 은/는 이미 존재하는 회원 번호입니다. 다른 회원 번호를 입력해주세요."),
    NOT_ENOUGH_POINT(HttpStatus.INTERNAL_SERVER_ERROR,"{0} 포인트가 부족합니다. 현재 잔액 포인트는 {1} 입니다."),
    ALREADY_CANCEL(HttpStatus.INTERNAL_SERVER_ERROR,"이미 취소된 포인트 번호 입니다."),
    IMPOSSIBLE_CANCEL(HttpStatus.INTERNAL_SERVER_ERROR,"취소가 불가능한 포인트 번호입니다."),
    ;
	private final HttpStatus httpStatus;
    private final String message;
}