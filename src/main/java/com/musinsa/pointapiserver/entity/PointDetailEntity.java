package com.musinsa.pointapiserver.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @class Name : PointDetailEntity.java
 * @Description : 포인트상세 엔티티
 * @Modification Information
 * @ Date			Author			Description
 * @ ------------	-------------	-------------
 * @ 2022. 8. 9.		나현지			최초 생성
 */
@Entity
@Table(name = "POINT_DETAIL")		// 포인트 상세 테이블
@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder(toBuilder = true)
public class PointDetailEntity extends AbstractEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Comment("포인트상세 순번")
	private Long pointDetailSn; 
	
	@ManyToOne
	@JoinColumn(name = "POINT_NO")
	@Comment("포인트 번호")
	private PointEntity pointNo; 
	
    @Comment("사용 관련 포인트번호")
    @Column(nullable = true)
    private Long refPointNo; 
    
	
	@Comment("포인트")
	@Column(precision = 19 ,scale = 0)
	private BigDecimal point;    
	   
    /**
     * @author 나현지
     * @date 2022. 8. 11.
     * @description 포인트 엔티티 set
     */
    public void updatePoint(PointEntity pointNo) {
        this.pointNo = pointNo;
    }
}
