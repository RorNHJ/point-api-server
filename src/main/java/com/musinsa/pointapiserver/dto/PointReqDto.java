package com.musinsa.pointapiserver.dto;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @class Name : PointReqDto.java
 * @Description :
 * @Modification Information
 * @ Date			Author			Description
 * @ ------------	-------------	-------------
 * @ 2022. 8. 11.		나현지			최초 생성
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PointReqDto {
    
    @NotNull(message = "회원번호는 필수값 입니다.")
    private Long memberNo;                 // 회원번호
    
    @Min(value = 1, message = "1원 이상 사용 또는 적립이 가능합니다. ")
    private BigDecimal point;               // 포인트
}
