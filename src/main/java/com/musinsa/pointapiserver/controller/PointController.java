package com.musinsa.pointapiserver.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musinsa.pointapiserver.dto.PointCnclReqDto;
import com.musinsa.pointapiserver.dto.PointDetailDto;
import com.musinsa.pointapiserver.dto.PointInfoDto;
import com.musinsa.pointapiserver.dto.PointReqDto;
import com.musinsa.pointapiserver.dto.PointResDto;
import com.musinsa.pointapiserver.service.PointService;


/**
 * @class Name : PointController.java
 * @Description : 포인트 조회, 충전, 차감, 소멸 컨트롤러
 * @Modification Information
 * @ Date			Author			Description
 * @ ------------	-------------	-------------
 * @ 2022. 7. 10.		나현지			최초 생성
 */
@RestController
@RequestMapping("/point")
public class PointController {
	
	@Autowired
	private PointService pointService;
	
	/**
	 * @author 나현지
	 * @date 2022. 8. 9.
	 * @description 회원별 포인트 합계 조회
	 */
	@GetMapping("/{memberNo}")
	public ResponseEntity<PointInfoDto> searchTotalPoint(@PathVariable("memberNo") Long memberNo) throws Exception{
		return 	ResponseEntity
		        .status(HttpStatus.OK)
		        .body(pointService.getTotalPoint(memberNo));
	}
	
	/**
     * @author 나현지
     * @date 2022. 8. 9.
     * @description 회원별 포인트 적립/사용 내역 조회 (페이징 처리 필수, 사용취소된 내역은 조회되지 않음)
     */
    @GetMapping("/history/{memberNo}")
    public ResponseEntity<List<PointDetailDto>> searchPointHistory(@PathVariable("memberNo") Long memberNo,Pageable pageable) throws Exception{
        return  ResponseEntity
                .status(HttpStatus.OK)
                .body(pointService.getPointHistory(memberNo,pageable));
    }
    
 
    /**
     * @author 나현지
     * @date 2022. 8. 11.
     * @description 포인트 적립
     */
    @PostMapping("/save") 
    public ResponseEntity<PointResDto> savePoint(@RequestBody @Valid PointReqDto pointReqDto) throws Exception{ 
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pointService.savePoint(pointReqDto)); 
    }
    
    /**
     * @author 나현지
     * @date 2022. 8. 11.
     * @description 포인트 사용
     */
    @PostMapping("/use") 
    public ResponseEntity<PointResDto> usePoint(@RequestBody @Valid PointReqDto pointReqDto) throws Exception{ 
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pointService.usePoint(pointReqDto)); 
    }
    
    
    /**
     * @author 나현지
     * @date 2022. 8. 13.
     * @description 포인트 사용 취소
     */
    @PostMapping("/cancel") 
    public ResponseEntity<PointResDto> cancelPoint(@RequestBody @Valid PointCnclReqDto pointCnclReqDto) throws Exception{ 
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pointService.cancelPoint(pointCnclReqDto)); 
    }
}
