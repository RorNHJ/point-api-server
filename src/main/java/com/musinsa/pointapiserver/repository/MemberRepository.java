package com.musinsa.pointapiserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.musinsa.pointapiserver.entity.MemberEntity;
@Repository
public interface MemberRepository  extends JpaRepository<MemberEntity, Long> {
}   