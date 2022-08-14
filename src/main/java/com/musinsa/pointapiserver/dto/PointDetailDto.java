package com.musinsa.pointapiserver.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @class Name : PointDetailDto.java
 * @Description : 포인트 상세 내역 return dto
 * @Modification Information
 * @ Date			Author			Description
 * @ ------------	-------------	-------------
 * @ 2022. 8. 9.		나현지			최초 생성
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PointDetailDto {
	private Long pointNo;              // 포인트 번호
	private String pointTypeCode;      // Point 유형 Code
	private String pointTypeName;      // Point 유형 name
	private BigDecimal point;          // 적립/사용 포인트
	private String expDe;            // 포인트 만료일자 yyyyMMdd
    private String content;            // 내용
    private String date;               // 적립/사용 일자
}
