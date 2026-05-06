package com.bank.pfe1.repository;

import com.bank.pfe1.entity.ParkingLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParkingLocationRepository extends JpaRepository<ParkingLocation, Long> {
    List<ParkingLocation> findByActive(boolean active);
    List<ParkingLocation> findByWilayaId(Long wilayaId);
    List<ParkingLocation> findByNameContainingIgnoreCase(String name);
}