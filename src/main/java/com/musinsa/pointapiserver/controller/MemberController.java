package com.musinsa.pointapiserver.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musinsa.pointapiserver.dto.MemberReqDto;
import com.musinsa.pointapiserver.dto.MemberResDto;
import com.musinsa.pointapiserver.service.MemberService;

/**
 * @class Name : MemberController.java
 * @Description : 회원 추가, 현재 모든 회원 조회
 * @Modification Information
 * @ Date			Author			Description
 * @ ------------	-------------	-------------
 * @ 2022. 8. 10.		나현지			최초 생성
 */
@RestController
@RequestMapping("/member")
public class MemberController {
	
	@Autowired
	private MemberService memberService;

    /**
     * @author 나현지
     * @date 2022. 8. 10.
     * @description 모든 회원 조회
     */
    @GetMapping
    public ResponseEntity<List<MemberResDto>> searchMemberList() throws Exception{
        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(memberService.getAllMemberList());
    }
    
	
	/**
	 * @author 나현지
	 * @date 2022. 8. 10.
	 * @description 회원 생성
	 */
	@PostMapping
	public ResponseEntity<MemberResDto> createMember(@RequestBody @Valid MemberReqDto memberReqDto) throws Exception{
	    return 	ResponseEntity
		        .status(HttpStatus.OK)
		        .body(memberService.createMember(memberReqDto));
	}
	
}
