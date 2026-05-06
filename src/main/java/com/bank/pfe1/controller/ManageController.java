package com.bank.pfe1.controller;

import com.bank.pfe1.entity.Manage;
import com.bank.pfe1.service.ManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/manage")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ManageController {

    private final ManageService manageService;

    @GetMapping("/history/employee/{employeeId}")
    public ResponseEntity<List<Manage>> getEmployeeHistory(@PathVariable Long employeeId) {
        return ResponseEntity.ok(manageService.getEmployeeHistory(employeeId));
    }

    @GetMapping("/history/vehicle/{vehicleId}")
    public ResponseEntity<List<Manage>> getVehicleHistory(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(manageService.getVehicleHistory(vehicleId));
    }

    @GetMapping("/current/employee/{employeeId}")
    public ResponseEntity<Manage> getCurrentAssignment(@PathVariable Long employeeId) {
        Manage current = manageService.getCurrentAssignment(employeeId);
        if (current != null) {
            return ResponseEntity.ok(current);
        }
        return ResponseEntity.noContent().build();
    }
}