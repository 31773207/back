package com.bank.pfe1.controller;

import com.bank.pfe1.entity.Vehicle;
import com.bank.pfe1.entity.VehicleStatus;
import com.bank.pfe1.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        return ResponseEntity.ok(vehicleService.getAllVehicles());
    }

    @GetMapping("/available")
    public ResponseEntity<List<Vehicle>> getAvailableVehicles() {
        return ResponseEntity.ok(vehicleService.getAvailableVehicles());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Vehicle>> getByStatus(@PathVariable VehicleStatus status) {
        return ResponseEntity.ok(vehicleService.getVehiclesByStatus(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getVehicleById(id));
    }


    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(vehicleService.createVehicle(vehicle));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(vehicleService.updateVehicle(id, vehicle));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Vehicle> updateStatus(@PathVariable Long id, @RequestParam VehicleStatus status) {
        return ResponseEntity.ok(vehicleService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.ok().build();
    }

    // ========== VEHICLE STATUS MANAGEMENT ==========

    @PatchMapping("/{id}/maintenance")
    public ResponseEntity<Vehicle> putInMaintenance(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.putInMaintenance(id));
    }

    @PatchMapping("/{id}/breakdown")
    public ResponseEntity<Vehicle> reportBreakdown(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.reportBreakdown(id));
    }

    @PatchMapping("/{id}/available")
    public ResponseEntity<Vehicle> markAvailable(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.markAvailable(id));
    }

    @PatchMapping("/{id}/reform")
    public ResponseEntity<Vehicle> reformVehicle(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.reformVehicle(id));
    }



}
