package com.bank.pfe1.service;

import com.bank.pfe1.entity.Employee;
import com.bank.pfe1.entity.Driver;
import com.bank.pfe1.entity.Vehicle;
import com.bank.pfe1.entity.VehicleStatus;
import com.bank.pfe1.repository.EmployeeRepository;
import com.bank.pfe1.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final VehicleRepository vehicleRepository;
    private final ManageService manageService;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    public List<Employee> getAllDrivers() {
        return employeeRepository.findAllDrivers();
    }

    public List<Employee> getAllEmployeesOnly() {
        return employeeRepository.findAllEmployees();
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        if (employee.getEmployeeType() == null) {
            employee.setEmployeeType("EMPLOYEE");
        }
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(Long id, Employee updated) {
        Employee employee = getEmployeeById(id);
        employee.setFirstName(updated.getFirstName());
        employee.setLastName(updated.getLastName());
        employee.setPhone(updated.getPhone());
        employee.setEmail(updated.getEmail());
        employee.setAddress(updated.getAddress());
        employee.setDateOfBirth(updated.getDateOfBirth());
        employee.setEmployeeType(updated.getEmployeeType());
        employee.setOrganization(updated.getOrganization());

        if (employee instanceof Driver && updated instanceof Driver) {
            Driver driver = (Driver) employee;
            Driver updatedDriver = (Driver) updated;
            driver.setLicenseNumber(updatedDriver.getLicenseNumber());
            driver.setLicenseExpiry(updatedDriver.getLicenseExpiry());
        }

        return employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    // ✅ FIXED: Add the date parameters to the method signature
    @Transactional
    public Employee assignVehicleToEmployee(Long employeeId, Long vehicleId, LocalDate startDate, LocalDate endDate) {
        Employee employee = getEmployeeById(employeeId);

        // Drivers cannot be directly assigned vehicles - only via Mission
        if ("DRIVER".equals(employee.getEmployeeType())) {
            throw new RuntimeException("Drivers cannot be directly assigned vehicles. Assign them via a Mission instead.");
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        // Check if vehicle is available
        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new RuntimeException("Vehicle is not available!");
        }

        // Create assignment history with dates
        manageService.createAssignmentHistory(employee, vehicle, startDate, endDate, "Assigned via Employee page");

        employee.setCurrentlyAssignedVehicle(vehicle);
        employee.setVehicleAssignedAt(startDate != null ? startDate.atStartOfDay() : LocalDateTime.now());
        employee.setVehicleRemovedAt(null);

        vehicle.setStatus(VehicleStatus.ASSIGNED);
        vehicle.setAssignedTo(employee);
        vehicle.setAssignedAt(LocalDateTime.now());
        vehicleRepository.save(vehicle);

        return employeeRepository.save(employee);
    }

    // Remove vehicle from employee
    @Transactional
    public Employee removeVehicleFromEmployee(Long employeeId) {
        Employee employee = getEmployeeById(employeeId);
        Vehicle vehicle = employee.getCurrentlyAssignedVehicle();

        if (vehicle != null) {
            // Complete the assignment history record
            manageService.completeAssignmentHistory(employee, vehicle);

            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicle.setAssignedTo(null);
            vehicleRepository.save(vehicle);
        }

        employee.setCurrentlyAssignedVehicle(null);
        employee.setVehicleRemovedAt(LocalDateTime.now());

        return employeeRepository.save(employee);
    }
}