package com.musinsa.pointapiserver.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @class Name : ChargePointDto.java
 * @Description : 포인트 충전 request Dto
 * @Modification Information
 * @ Date           Author          Description
 * @ ------------   -------------   -------------
 * @ 2022. 7. 10.       나현지         최초 생성
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PointResDto {
    private Long pointNo;                // 적립 또는 사용 포인트 번호
    private BigDecimal beforePoint;      // 적립 또는 사용 전 포인트
    private BigDecimal afterPoint;       // 적립 또는 사용 후 포인트
}
