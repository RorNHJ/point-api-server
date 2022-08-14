package com.musinsa.pointapiserver.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @class Name : AbstractEntity.java
 * @Description : 엔티티 공통 필드
 * @Modification Information
 * @ Date			Author			Description
 * @ ------------	-------------	-------------
 * @ 2022. 8. 9.        나현지         최초 생성
 */


@Getter
@MappedSuperclass
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity  {
	
	@CreatedDate
	@Column(name = "FRST_CRE_DT", nullable = false, updatable = false)
	@Comment("최초 생성 일시")
	private LocalDateTime  frstCreDt;
	
	@Column(name = "LAST_UPD_DT", nullable = false )
	@LastModifiedDate
	@Comment("최종 수정 일시")
	private LocalDateTime  lastUpdDt;

	
	
}