package com.musinsa.pointapiserver.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @class Name : MemberEntity.java
 * @Description : 회원 엔티티
 * @Modification Information
 * @ Date			Author			Description
 * @ ------------	-------------	-------------
 * @ 2022. 8. 9.        나현지         최초 생성
 */

@Entity
@Table(name = "MEMBER")		// 회원 테이블
@NoArgsConstructor
@AllArgsConstructor
@Getter
@SuperBuilder(toBuilder = true)
public class MemberEntity extends AbstractEntity {
	@Id
	@Comment("회원 번호")
	private Long memberNo;
	
	  
    @OneToMany(mappedBy = "memberNo",cascade = CascadeType.ALL) // get 시점에 조회
    private List<PointEntity> point = new ArrayList<PointEntity>();
    
    public void insertPoint(PointEntity point) {
        if(this.point == null ) this.point =  new ArrayList<PointEntity>();
        this.point.add(point);
        point.updateMember(this);
    }

}
