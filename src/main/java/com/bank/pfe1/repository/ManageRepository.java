package com.bank.pfe1.repository;

import com.bank.pfe1.entity.Manage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ManageRepository extends JpaRepository<Manage, Long> {

    List<Manage> findByEmployeeId(Long employeeId);
    List<Manage> findByVehicleId(Long vehicleId);

    // Find active assignment (not removed yet)
    @Query("SELECT m FROM Manage m WHERE m.employee.id = :employeeId AND m.removedAt IS NULL")
    List<Manage> findActiveAssignmentsByEmployeeId(Long employeeId);

    // Find assignment history for a vehicle
    List<Manage> findByVehicleIdOrderByAssignedAtDesc(Long vehicleId);

    // Find assignment history for an employee
    List<Manage> findByEmployeeIdOrderByAssignedAtDesc(Long employeeId);
}