package com.bank.pfe1.service;

import com.bank.pfe1.entity.*;
import com.bank.pfe1.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalDate;  // ✅ ADD THIS
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManageService {

    private final ManageRepository manageRepository;
    private final EmployeeRepository employeeRepository;
    private final VehicleRepository vehicleRepository;

    // Create assignment history record
    @Transactional
    public Manage createAssignmentHistory(Employee employee, Vehicle vehicle, LocalDate startDate, LocalDate endDate, String notes) {
        Manage manage = new Manage();
        manage.setEmployee(employee);
        manage.setVehicle(vehicle);
        manage.setAssignedAt(LocalDateTime.now());
        manage.setStartDate(startDate);
        manage.setEndDate(endDate);
        manage.setNotes(notes);
        return manageRepository.save(manage);
    }

    // ✅ Keep this for backward compatibility (if needed)
    @Transactional
    public Manage createAssignmentHistory(Employee employee, Vehicle vehicle, String notes) {
        return createAssignmentHistory(employee, vehicle, null, null, notes);
    }

    // Complete assignment history
    @Transactional
    public Manage completeAssignmentHistory(Employee employee, Vehicle vehicle) {
        List<Manage> manages = manageRepository.findActiveAssignmentsByEmployeeId(employee.getId());
        if (!manages.isEmpty()) {
            Manage manage = manages.get(0);
            manage.setRemovedAt(LocalDateTime.now());  // ✅ This sets the removal date
            return manageRepository.save(manage);
        }
        return null;
    }

    // Get full history for an employee
    public List<Manage> getEmployeeHistory(Long employeeId) {
        return manageRepository.findByEmployeeIdOrderByAssignedAtDesc(employeeId);
    }

    // Get full history for a vehicle
    public List<Manage> getVehicleHistory(Long vehicleId) {
        return manageRepository.findByVehicleIdOrderByAssignedAtDesc(vehicleId);
    }

    // Get current assignment for an employee
    public Manage getCurrentAssignment(Long employeeId) {
        List<Manage> manages = manageRepository.findActiveAssignmentsByEmployeeId(employeeId);
        if (!manages.isEmpty()) {
            return manages.get(0);
        }
        return null;
    }


}
