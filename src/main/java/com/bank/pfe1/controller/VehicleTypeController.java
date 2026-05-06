package com.bank.pfe1.controller;

import com.bank.pfe1.entity.VehicleType;
import com.bank.pfe1.service.VehicleTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vehicle-types")
@RequiredArgsConstructor
public class VehicleTypeController {

    private final VehicleTypeService vehicleTypeService;

    @GetMapping
    public ResponseEntity<List<VehicleType>> getAllTypes() {
        return ResponseEntity.ok(vehicleTypeService.getAllTypes());
    }

    @PostMapping
    public ResponseEntity<VehicleType> createType(@RequestBody VehicleType type) {
        return ResponseEntity.ok(vehicleTypeService.createType(type));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleType> updateType(@PathVariable Long id, @RequestBody VehicleType type) {
        return ResponseEntity.ok(vehicleTypeService.updateType(id, type));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteType(@PathVariable Long id) {
        vehicleTypeService.deleteType(id);
        return ResponseEntity.ok().build();
    }
}