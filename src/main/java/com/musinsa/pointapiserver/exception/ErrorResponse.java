package com.musinsa.pointapiserver.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.musinsa.pointapiserver.code.ErrorCode;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private final String code;
    private final String message;

    
    public static ResponseEntity toResponseEntity(HttpStatus httpStatus,String message) {
    	return ResponseEntity
                .status(httpStatus)
                .body(ErrorResponse.builder()
			                        .status(httpStatus.value())
			                        .error(httpStatus.name())
			                        .code(httpStatus.name())
			                        .message(message)
			                        .build()
                );
    }
    
    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
    	return ResponseEntity
    			.status(errorCode.getHttpStatus())
    			.body(ErrorResponse.builder()
    								.status(errorCode.getHttpStatus().value())
    								.error(errorCode.name())
    								.code(errorCode.name())
    								.message(errorCode.getMessage())
    								.build()
                );
    }
    
    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode,String... parameters) {
    	String newErrorMsg = errorCode.getMessage();
		for(int i=0; i<parameters.length; i++) {
			newErrorMsg = newErrorMsg.replace("{"+i+"}",parameters[i]);
		}
    	return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
			                        .status(errorCode.getHttpStatus().value())
			                        .error(errorCode.name())
			                        .code(errorCode.name())
			                        .message(newErrorMsg)
			                        .build()
                );
    }
    
    
}