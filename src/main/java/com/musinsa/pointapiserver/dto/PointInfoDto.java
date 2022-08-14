package com.musinsa.pointapiserver.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @class Name : PointInfoDto.java
 * @Description : 포인트 정보 return dto
 * @Modification Information
 * @ Date			Author			Description
 * @ ------------	-------------	-------------
 * @ 2022. 8. 9.		나현지			최초 생성
 */
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PointInfoDto {
	private Long memberNo;                             // 회원번호
	private BigDecimal totalPoint;                     // 포인트(현재 총 포인트)
}
