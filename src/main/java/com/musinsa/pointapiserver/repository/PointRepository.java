package com.musinsa.pointapiserver.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.musinsa.pointapiserver.code.PointType;
import com.musinsa.pointapiserver.entity.MemberEntity;
import com.musinsa.pointapiserver.entity.PointDetailEntity;
import com.musinsa.pointapiserver.entity.PointEntity;
@Repository
public interface PointRepository  extends JpaRepository<PointEntity, Long> {
	
	/* 회원번호 포인트 조회*/
	Optional<PointEntity> findOneByMemberNo(MemberEntity memberNo);
	
    /* 회원번호 포인트유형이 적립이고, 만료일자가 유효한 포인트이고, 남은 잔액이 0원이 아닌 포인트 조회*/
    List<PointEntity> findAllByMemberNoAndExpDeGreaterThanEqualAndRemainPointGreaterThanOrderByPointNo(MemberEntity memberNo, String expDe, BigDecimal remainPoint);
    
    /* 포인트 내역조회( 페이징처리, 사용취소된건은 제외*/
    Page<PointEntity> findAllByMemberNoAndPointTypeNotOrderByPointNoDesc(MemberEntity memberNo ,PointType pointType,Pageable pageable );
  
    
}