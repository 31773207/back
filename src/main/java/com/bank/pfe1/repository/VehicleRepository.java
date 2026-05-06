package com.bank.pfe1.repository;

import com.bank.pfe1.entity.Vehicle;
import com.bank.pfe1.entity.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByPlateNumber(String plateNumber);

    // Find vehicles by status
    List<Vehicle> findByStatus(VehicleStatus status);

    // Find vehicles assigned to an employee
    @Query("SELECT v FROM Vehicle v WHERE v.assignedTo IS NOT NULL")
    List<Vehicle> findAssignedVehicles();

    // Find available vehicles (status AVAILABLE and not assigned to anyone)
    @Query("SELECT v FROM Vehicle v WHERE v.status = 'AVAILABLE' AND v.assignedTo IS NULL")
    List<Vehicle> findAvailableVehicles();

    // Find vehicles by assigned employee
    List<Vehicle> findByAssignedToId(Long employeeId);
}