package com.musinsa.pointapiserver.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.musinsa.pointapiserver.code.ErrorCode;
import com.musinsa.pointapiserver.code.PointType;
import com.musinsa.pointapiserver.dto.PointCnclReqDto;
import com.musinsa.pointapiserver.dto.PointDetailDto;
import com.musinsa.pointapiserver.dto.PointInfoDto;
import com.musinsa.pointapiserver.dto.PointReqDto;
import com.musinsa.pointapiserver.dto.PointResDto;
import com.musinsa.pointapiserver.entity.MemberEntity;
import com.musinsa.pointapiserver.entity.PointDetailEntity;
import com.musinsa.pointapiserver.entity.PointEntity;
import com.musinsa.pointapiserver.exception.BizException;
import com.musinsa.pointapiserver.repository.MemberRepository;
import com.musinsa.pointapiserver.repository.PointRepository;
/**
 * @class Name : PointService.java
 * @Description : 포인트 CRUD 서비스
 * @Modification Information
 * @ Date			Author			Description
 * @ ------------	-------------	-------------
 * @ 2022. 8. 9.		나현지			최초 생성
 */
@Service
public class PointService {

	@Autowired
	private PointRepository pointRepository;
	
    @Autowired
    private MemberRepository memberRepository;
    
    
	/**
	 * @author 나현지
	 * @date 2022. 8. 10.
	 * @description 회원별 포인트 합계와 사용가능한 포인트 목록 조회
	 * 1. 회원 조회
	 * 2. 엔티티로부터 조회용 Dto 생성
	 */
	public PointInfoDto  getTotalPoint(Long memberNo) {
		
	    /** 회원 조회 */
	    Optional<MemberEntity> memberOpt = memberRepository.findById(memberNo);
	    memberOpt.orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_DATA, "회원 정보"));
	    
		/** 회원의 포인트 조회 */
		List<PointEntity> pointEntityList = pointRepository.findAllByMemberNoAndExpDeGreaterThanEqualAndRemainPointGreaterThanOrderByPointNo(memberOpt.get(),LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), BigDecimal.ZERO);	
		
		BigDecimal sum = pointEntityList.stream().map( e -> e.getRemainPoint())
		                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
		
		/** 엔티티로부터 조회용 Dto 생성 */
		return PointInfoDto.builder()
                            .memberNo(memberNo)
                            .totalPoint(sum)
                            .build();
	}
	
	
	
    /**
     * @author 나현지
     * @date 2022. 8. 11.
     * @description 포인트 적립/사용 내역 조회( 사용취소된 건 제외, 페이징)
     * 1. 회원 조회
     * 2. 포인트 조회
     * 2. 엔티티로부터 조회용 Dto 생성
     */
    public List<PointDetailDto>  getPointHistory(Long memberNo,Pageable pageable) {
        
        /** 회원 조회 */
        Optional<MemberEntity> memberOpt = memberRepository.findById(memberNo);
        memberOpt.orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_DATA, "회원 정보"));
        
        
        /** 회원의 포인트 조회 */
        Page<PointEntity> resultList = pointRepository.findAllByMemberNoAndPointTypeNotOrderByPointNoDesc(memberOpt.get(),PointType.CANCLE,pageable);    
        
        /** 엔티티로부터 조회용 Dto 생성 */
        return resultList.stream()
                          .map( e -> PointDetailDto.builder()
                                                    .pointNo(e.getPointNo())
                                                    .pointTypeCode(e.getPointType().getCode())
                                                    .pointTypeName(e.getPointType().getCode())
                                                    .expDe(e.getExpDe())
                                                    .content(e.getContent())
                                                    .point(e.getPoint())
                                                    .date(e.getFrstCreDt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                                    .build())
                          .collect(Collectors.toList());
        
    }
    
	
	/**
     * @author 나현지
     * @date 2022. 8. 11.
     * @description 포인트 적립 
     * 1. 회원 조회
     * 2. 포인트 엔티티 생성 
     * 3. 회원 - 포인트 엔티티 set
     * 5. 포인트 적립 내역 엔티티 생성 
     * 6. 회원, 포인트 , 포인트 상세 저장
     * 7. 적립 전 포인트, 적립 후 포인트를 응답
     */
    @Transactional
    public PointResDto savePoint(PointReqDto pointReqDto) {
        
        Long memberNo = pointReqDto.getMemberNo();      // 회원번호
        BigDecimal reqPoint = pointReqDto.getPoint();   // 요청한 포인트
        
        BigDecimal beforePoint = getTotalPoint(memberNo).getTotalPoint();   // 적립 전, 회원별 포인트 합계 
        BigDecimal afterPoint = beforePoint.add(reqPoint);                  // 적립 후, 회원별 포인트 합계 
        
        PointEntity pointEntity;                                    // 포인트 엔티티
        PointDetailEntity pointDetailEntity;                        // 포인트 상세 엔티티
        
        /** 회원 조회 */
        Optional<MemberEntity> memberOpt = memberRepository.findById(memberNo);
        memberOpt.orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_DATA, "회원 정보"));
        
        /** 포인트 엔티티 생성  */
        pointEntity = PointEntity.builder()
                                 .pointType(PointType.SAVE)
                                 .point(PointType.SAVE.calculate(reqPoint))
                                 .remainPoint(reqPoint)
                                 .expDe(LocalDateTime.now().plusYears(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                                 .content("구매 적립")
                                 .build();
        
        
        
        
        /** 포인트 적립 내역 엔티티 생성  */ 
        pointDetailEntity = PointDetailEntity.builder()
                                                                .point(PointType.SAVE.calculate(reqPoint))
                                                                .build();
        
        /** 회원, 포인트 , 포인트 상세 저장*/
        memberOpt.get().insertPoint(pointEntity);
        pointEntity.insertPointDetail(pointDetailEntity);
        memberRepository.flush();
        
        /** 적립 전 포인트, 적립 후 포인트를 응답 */
        return PointResDto.builder()
                          .pointNo(pointEntity.getPointNo())
                          .beforePoint(beforePoint)
                          .afterPoint(afterPoint)
                          .build();
    }



    /**
     * @author 나현지
     * @date 2022. 8. 13.
     * @description 포인트 사용
     * 1. 사용가능한 포인트 조회
     * 2. 사용가능한 포인트의 합계가 요청포인트보다 작을 경우 포인트 부족으로 에러처리
     * 3. 회원 조회 및 회원의 사용가능한 관련 포인트 조회
     * 4. 사용 포인트 엔티티 생성
     * 5. 차감하고 남은 포인트의 잔액포인트 계산 및 포인트 사용 내역 생성
     * 6. 사용포인트번호, 사용 전 포인트, 사용 후 포인트를 응답
     */
    
    @Transactional
    public PointResDto usePoint( PointReqDto pointReqDto) {
        
        Long memberNo = pointReqDto.getMemberNo();      // 회원번호
        BigDecimal reqPoint = pointReqDto.getPoint();   // 요청한 포인트
        BigDecimal beforePoint, afterPoint;             // 사용 전 포인트, 사용후 포인트
        
        PointEntity pointEntity;                                                                // 포인트 엔티티
        List<PointDetailEntity> pointDetailEntityList = new ArrayList<PointDetailEntity>();     // 포인트 상세 엔티티
        
        /** 사용가능한 포인트 조회 */
        PointInfoDto pointInfoDto = getTotalPoint(memberNo);
        
        
        /** 사용가능한 포인트의 합계가 요청포인트보다 작을 경우 포인트 부족으로 에러처리 */
        BigDecimal usePossiblePoint = pointInfoDto.getTotalPoint();  // 사용가능한 포인트 총합
        beforePoint = usePossiblePoint;
        afterPoint = usePossiblePoint.subtract(reqPoint);
        
        if(usePossiblePoint.compareTo(reqPoint) < 0) {
            throw new BizException(ErrorCode.NOT_ENOUGH_POINT, new String[] {reqPoint.subtract(usePossiblePoint).toString(),usePossiblePoint.toString()}) ;   // {0} 포인트가 부족합니다. 현재 잔액 포인트는 {1} 입니다
        }
        
        
        /** 회원 조회 및 회원의 사용가능한 관련 포인트 조회 */
        Optional<MemberEntity> memberOpt = memberRepository.findById(memberNo);
        memberOpt.orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_DATA, "회원 정보"));
        
        List<PointEntity> refPointEntityList = pointRepository.findAllByMemberNoAndExpDeGreaterThanEqualAndRemainPointGreaterThanOrderByPointNo(memberOpt.get(),LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), BigDecimal.ZERO);    
        
        
        /** 사용 포인트 엔티티 생성  */
        pointEntity = PointEntity.builder()
                                .pointType(PointType.USE)
                                .point(PointType.USE.calculate(reqPoint))
                                .remainPoint(BigDecimal.ZERO)
                                .content("고객에 의한 사용")
                                .build();
        
        
        
        /** 차감하고 남은 포인트의 잔액포인트 계산 및 포인트 사용 내역 생성*/
        for(PointEntity refPointEntity : refPointEntityList) {
            if(reqPoint.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal usedPoint = refPointEntity.use(reqPoint);
                
                PointDetailEntity pointDetailEntity = PointDetailEntity.builder()
                                                                        .point(PointType.USE.calculate(usedPoint))
                                                                        .refPointNo(refPointEntity.getPointNo())
                                                                        .build();
                pointDetailEntityList.add(pointDetailEntity);
                reqPoint = reqPoint.subtract(usedPoint);
                
            }
        }
        
        /** 회원, 포인트 , 포인트 상세 저장 */
        memberOpt.get().insertPoint(pointEntity);
        pointEntity.insertPointDetail(pointDetailEntityList);
        memberRepository.flush();
        
        /** 사용 전 포인트, 사용 후 포인트를 응답 */
        return PointResDto.builder()
                            .pointNo(pointEntity.getPointNo())
                            .beforePoint(beforePoint)
                            .afterPoint(afterPoint)
                            .build();
    }   
    
    /**
     * @author 나현지
     * @date 2022. 8. 13.
     * @description 포인트 사용 취소
     * 1. 회원 조회
     * 2. 포인트, 내역 조회
     * 3. 사용취소 포인트 엔티티 생성
     * 4. 사용했던 포인트 번호들 다시 더해서 업데이트
     * 5. 사용취소포인트번호, 적립 전 포인트, 적립 후 포인트를 응답
     */
    @Transactional
    public PointResDto cancelPoint( PointCnclReqDto pointReqDto) {
        
        Long memberNo = pointReqDto.getMemberNo();      // 회원번호
        Long reqPointNo = pointReqDto.getPointNo();     // 사용 취소할 포인트 번호
        BigDecimal beforePoint, afterPoint;             // 취소 전 포인트, 취소 후 포인트
        
        PointEntity pointEntity;
        List<PointDetailEntity> pointDetailEntityList = new ArrayList<PointDetailEntity>();     // 포인트 상세 엔티티
        
        beforePoint = getTotalPoint(memberNo).getTotalPoint();
        
        /** 회원 조회 */
        Optional<MemberEntity> memberOpt = memberRepository.findById(memberNo);
        memberOpt.orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_DATA, "회원 정보"));   //해당 {0} 이/가 존재하지 않습니다.
        
        /** 포인트, 내역 조회*/
        Optional<PointEntity> pointOpt = pointRepository.findById(reqPointNo);
        pointOpt.orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND_DATA, "포인트 정보"));   //해당 {0} 이/가 존재하지 않습니다.
        pointEntity = pointOpt.get();
        
        //이미 취소된 포인트번호이면 에러처리
        if(pointEntity.getPointType().equals(PointType.CANCLE)) throw new BizException(ErrorCode.ALREADY_CANCEL);   //이미 취소된 포인트 번호 입니다.
        //포인트 유형이 사용이 아닌 적립이거나 그 외일 경우 에러처리
        if(!pointEntity.getPointType().equals(PointType.USE))   throw new BizException(ErrorCode.IMPOSSIBLE_CANCEL);   //취소가 불가능한 포인트 번호입니다.
        
        List<PointDetailEntity> pointDetailList = pointEntity.getPointDetailList();
        afterPoint = beforePoint.add(PointType.CANCLE.calculate(pointEntity.getPoint()));
        
        /** 사용했던 포인트 번호들 다시 더해서 업데이트 */
        for(PointDetailEntity pointDetail : pointDetailList) {
            Optional<PointEntity> refPointOpt =  pointRepository.findById(pointDetail.getRefPointNo());
            refPointOpt.get().cancel(PointType.CANCLE.calculate(pointDetail.getPoint()));
            
            
            PointDetailEntity pointDetailEntity = PointDetailEntity.builder()
                                                                    .point(PointType.CANCLE.calculate(pointDetail.getPoint()))
                                                                    .refPointNo(pointDetail.getRefPointNo())
                                                                    .build();
            pointDetailEntityList.add(pointDetailEntity);
        }
        
        
        /** 회원, 포인트 , 포인트 상세 저장, 포인트유형 변경 */
        pointEntity.insertPointDetail(pointDetailEntityList);
        pointEntity.updatePointTypeUseToCancel("구매 취소에 의한 사용 취소");
        memberRepository.flush();
        
        /** 적립 전 포인트, 적립 후 포인트를 응답 */
        return PointResDto.builder()
                                .pointNo(pointEntity.getPointNo())
                                .beforePoint(beforePoint)
                                .afterPoint(afterPoint)
                                .build();
    }   
}
