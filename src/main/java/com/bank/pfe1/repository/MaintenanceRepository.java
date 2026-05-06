package com.bank.pfe1.repository;

import com.bank.pfe1.entity.Maintenance;
import com.bank.pfe1.entity.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    List<Maintenance> findByVehicleId(Long vehicleId);

    List<Maintenance> findByStatus(MaintenanceStatus status);

    List<Maintenance> findByMaintenanceType(String type);

    List<Maintenance> findByStartDateBetween(LocalDate start, LocalDate end);

    long countByStatus(MaintenanceStatus status);
}