package com.musinsa.pointapiserver.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.musinsa.pointapiserver.code.ErrorCode;
import com.musinsa.pointapiserver.dto.MemberReqDto;
import com.musinsa.pointapiserver.dto.MemberResDto;
import com.musinsa.pointapiserver.entity.MemberEntity;
import com.musinsa.pointapiserver.exception.BizException;
import com.musinsa.pointapiserver.repository.MemberRepository;
/**
 * @class Name : PointService.java
 * @Description : 포인트 CRUD 서비스
 * @Modification Information
 * @ Date			Author			Description
 * @ ------------	-------------	-------------
 * @ 2022. 8. 9.		나현지			최초 생성
 */
@Service
public class MemberService {

	@Autowired
	private MemberRepository memberRepository;
	    
    /**
     * @author 나현지
     * @date 2022. 8. 10.
     * @description 등록된 모든 회원 조회
     */
    public List<MemberResDto>  getAllMemberList() {
        List<MemberEntity> list = memberRepository.findAll();   
        return list.stream().map( e -> MemberResDto.builder().memberNo(e.getMemberNo()).build() )
                            .collect(Collectors.toList());
    }
    
	/**
	 * @author 나현지
	 * @date 2022. 8. 10.
	 * @description 회원 생성
	 * 1. 기존에 있는 중복 회원번호인지 조회
	 * 2. 회원 엔티티 생성
	 * 3. 포인트 엔티티 생성
	 * 4. 회원 - 포인트 엔티티 set
	 */
	@Transactional
    public MemberResDto createMember(MemberReqDto memberReqDto) throws Exception  {
        
	    /** 기존에 있는 중복 회원번호인지 조회 */
	    Optional<MemberEntity> memberOpt = memberRepository.findById(memberReqDto.getMemberNo());
	    if(memberOpt.isPresent()) throw new BizException(ErrorCode.DUPLICATE_MEMBER_NO, memberReqDto.getMemberNo().toString());

	    
	    /** 회원 엔티티 생성 */
        MemberEntity memberEntity = MemberEntity.builder()
                                                .memberNo(memberReqDto.getMemberNo())
                                                .build();
        
        
        
        /** 저장 ( 회원, 포인트)*/
        memberEntity= memberRepository.save(memberEntity);  
        
        return MemberResDto.builder()
                            .memberNo(memberEntity.getMemberNo())
                            .build();

    }
	
}
