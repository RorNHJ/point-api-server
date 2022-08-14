package com.musinsa.pointapiserver.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @class Name : MemberReqDto.java
 * @Description : 회원생성 요청 dto
 * @Modification Information
 * @ Date			Author			Description
 * @ ------------	-------------	-------------
 * @ 2022. 8. 10.		나현지			최초 생성
 */


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MemberReqDto {
    @NotNull(message = "회원번호는 필수값 입니다.")
    private Long memberNo;                 // 회원번호
    
}
