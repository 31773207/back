package com.bank.pfe1.repository;

import com.bank.pfe1.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {
    boolean existsByTypeName(String typeName);
}