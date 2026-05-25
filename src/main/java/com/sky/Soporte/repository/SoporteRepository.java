package com.sky.Soporte.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sky.Soporte.model.Soporte;

@Repository
public interface SoporteRepository extends  JpaRepository <Soporte, Long>{

}
