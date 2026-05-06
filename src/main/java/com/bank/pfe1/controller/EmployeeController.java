package com.bank.pfe1.controller;

import com.bank.pfe1.entity.Employee;
import com.bank.pfe1.service.EmployeeService;
import com.bank.pfe1.dto.VehicleAssignmentRequest;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeService employeeService;

    // Get all employees (both Drivers and Employees)
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // Get only Drivers
    @GetMapping("/drivers")
    public ResponseEntity<List<Employee>> getAllDrivers() {
        return ResponseEntity.ok(employeeService.getAllDrivers());
    }

    // Get only Employees (non-drivers)
    @GetMapping("/employees-only")
    public ResponseEntity<List<Employee>> getAllEmployeesOnly() {
        return ResponseEntity.ok(employeeService.getAllEmployeesOnly());
    }

    // Get employee by ID
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    // Create new employee
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.createEmployee(employee));
    }

    // Update employee
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, employee));
    }

    // Delete employee
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // Assign vehicle to employee (for daily use, not mission)
    @PostMapping("/{employeeId}/assign-vehicle/{vehicleId}")
    public ResponseEntity<Employee> assignVehicleToEmployee(
            @PathVariable Long employeeId,
            @PathVariable Long vehicleId,
            @RequestBody VehicleAssignmentRequest request) {

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        return ResponseEntity.ok(employeeService.assignVehicleToEmployee(employeeId, vehicleId, startDate, endDate));
    }

    // Remove vehicle from employee
    @DeleteMapping("/{employeeId}/remove-vehicle")
    public ResponseEntity<Employee> removeVehicleFromEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeService.removeVehicleFromEmployee(employeeId));
    }
}
