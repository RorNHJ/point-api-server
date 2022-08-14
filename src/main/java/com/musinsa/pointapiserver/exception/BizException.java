package com.musinsa.pointapiserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.musinsa.pointapiserver.code.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class BizException  extends RuntimeException {
	private final ErrorCode errorCode;
	private final String[] parameters;
	
	public BizException() {
        this.errorCode = ErrorCode.TRY_AGAIN_LATER;
        this.parameters = null;
    }
    
	
	public BizException(ErrorCode errorCode) {
		this.errorCode = errorCode;
		this.parameters = null;
	}
	
	public BizException(ErrorCode errorCode , String parameter) {
		this.errorCode = errorCode;
		this.parameters = new String[] {parameter};
		
	}
}