package com.musinsa.pointapiserver.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @class Name : PointCnclReqDto.java
 * @Description : 포인트사용취소 요청 dto
 * @Modification Information
 * @ Date			Author			Description
 * @ ------------	-------------	-------------
 * @ 2022. 8. 13.		나현지			최초 생성
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class PointCnclReqDto {
    
    @NotNull(message = "회원번호는 필수값 입니다.")
    private Long memberNo;                 // 회원번호
    
    @NotNull(message = "사용취소할 포인트 번호는 필수값 입니다..")
    private Long pointNo;                   // 포인트 번호

}
