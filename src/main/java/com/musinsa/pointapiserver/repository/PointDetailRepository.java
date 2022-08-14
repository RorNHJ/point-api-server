package com.musinsa.pointapiserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.musinsa.pointapiserver.entity.PointDetailEntity;
@Repository
public interface PointDetailRepository  extends JpaRepository<PointDetailEntity, Long> {
    
}