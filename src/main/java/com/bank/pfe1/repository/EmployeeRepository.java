package com.bank.pfe1.repository;

import com.bank.pfe1.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByEmployeeType(String employeeType);

    @Query("SELECT e FROM Employee e WHERE e.employeeType = 'DRIVER'")
    List<Employee> findAllDrivers();

    @Query("SELECT e FROM Employee e WHERE e.employeeType = 'EMPLOYEE'")
    List<Employee> findAllEmployees();

    List<Employee> findByCurrentlyAssignedVehicleIsNotNull();

    List<Employee> findByCurrentlyAssignedVehicleIsNull();
}
