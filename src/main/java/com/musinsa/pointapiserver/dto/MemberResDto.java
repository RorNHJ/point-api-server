package com.musinsa.pointapiserver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @class Name : MemberReqDto.java
 * @Description : 회원생성 요청,응답 dto
 * @Modification Information
 * @ Date			Author			Description
 * @ ------------	-------------	-------------
 * @ 2022. 8. 10.		나현지			최초 생성
 */


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class MemberResDto {
    private Long memberNo;                 // 회원번호
}
