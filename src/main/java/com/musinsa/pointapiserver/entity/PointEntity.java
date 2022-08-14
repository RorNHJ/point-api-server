package com.musinsa.pointapiserver.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import com.musinsa.pointapiserver.code.PointType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @class Name : PointEntity.java
 * @Description : 포인트 엔티티
 * @Modification Information
 * @ Date			Author			Description
 * @ ------------	-------------	-------------
 * @ 2022. 8. 9.        나현지         최초 생성
 */
@Entity
@Table(name = "POINT")		// 포인트 테이블
@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder(toBuilder = true)
public class PointEntity extends AbstractEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Comment("포인트 번호")
	private Long pointNo; 
	
	@OneToOne
	@JoinColumn(name = "MEMBER_NO" )
	@Comment("회원 번호")
	private MemberEntity memberNo;
	
    @Enumerated(EnumType.STRING)
    @Comment("포인트 유형")
    private PointType pointType;    // 유형 ( 사용취소, 적립, 소멸, 사용) PointType	
    
	@Comment("포인트(적립/사용)")
	@Column(precision = 19 ,scale = 0)
	private BigDecimal point;   
	
    @Comment("잔액 포인트")
    @Column(precision = 19 ,scale = 0 )
    private BigDecimal remainPoint;   
    
    
    @Comment("만료일자-yyyyMMdd")
    @Column(nullable = true)
    private String expDe;  	
    
    @Comment("내용")
    @Column(nullable = true)
    private String content;  
	
    @Comment("취소 사유")
    @Column(nullable = true)
    private String cnclRsn;  
    
    @Comment("취소 일시")
    @Column(nullable = true)
    private LocalDateTime cnclDt;  
    
	@OneToMany(mappedBy = "pointNo", cascade = CascadeType.ALL)	// get 시점에 조회
	private List<PointDetailEntity> pointDetailList = new ArrayList<>();
	
	
	
	/**
	 * @author 나현지
	 * @date 2022. 8. 11.
	 * @description 회원 엔티티 set
	 */
	public void updateMember(MemberEntity memberNo) {
	    this.memberNo = memberNo;
	}
	
	
	   
    /**
     * @author 나현지
     * @date 2022. 8. 11.
     * @description 포인트 사용
     * @return   잔액포인트에서 사용한 포인트
     */
    public BigDecimal use(BigDecimal reqPoint) {
        
        BigDecimal usedPoint = BigDecimal.ZERO;
        
        // 잔액 포인트가 있을 경우에만 계산
        if(this.remainPoint.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal calRemainPoint = this.remainPoint.subtract(reqPoint); // 잔액포인트에서 요청포인트 차감
            
            // 계산하고 남은 포인트가 마이너스 금액 이면 0으로 업데이트
            // 아니면 계산하고 남은포인트로 업데이트. 계산하고 남으면 포인트는 0으로 리턴
            if(calRemainPoint.compareTo(BigDecimal.ZERO) < 0) { 
                usedPoint = this.remainPoint;
                this.remainPoint = BigDecimal.ZERO;
            }
            else{
                this.remainPoint = calRemainPoint;
                usedPoint = reqPoint;
            }
        }
        return usedPoint; 
    }
    
    /**
     * @author 나현지
     * @date 2022. 8. 11.
     * @description 포인트 사용 취소 ,잔액포인트에서 취소될 포인트 더함
     * @return   
     */
    public void cancel(BigDecimal reqPoint) {
        this.remainPoint = this.remainPoint.add(reqPoint);
    }
       
    
    /**
     * @author 나현지
     * @date 2022. 8. 13.
     * @description 포인트 내역 추가
     */
    public void insertPointDetail(PointDetailEntity pointDetailEntity) {
        if(pointDetailList == null) pointDetailList = new ArrayList<PointDetailEntity>();
        pointDetailList.add(pointDetailEntity);
        pointDetailEntity.updatePoint(this);
    }
    
    /**
     * @author 나현지
     * @date 2022. 8. 13.
     * @description 포인트 내역 추가
     */
    public void insertPointDetail(List<PointDetailEntity>  pointDetailEntityList) {
        if(pointDetailList == null) pointDetailList = new ArrayList<PointDetailEntity>();
        pointDetailList.addAll(pointDetailEntityList);
        for(PointDetailEntity e : pointDetailEntityList)  e.updatePoint(this);
    }
    
    
    /**
     * @author 나현지
     * @date 2022. 8. 13.
     * @description 포인트 사용 -> 사용취소로 업데이트
     */
    public void updatePointTypeUseToCancel(String cnclRsn) {
        this.cnclRsn = cnclRsn;
        this.cnclDt = LocalDateTime.now();
        this.pointType = PointType.CANCLE;
    }
	  
}
