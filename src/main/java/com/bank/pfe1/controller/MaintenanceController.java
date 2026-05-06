package com.bank.pfe1.controller;

import com.bank.pfe1.entity.Maintenance;
import com.bank.pfe1.entity.MaintenanceStatus;
import com.bank.pfe1.service.MaintenanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MaintenanceController {

    private final MaintenanceService service;

    // ===== CRUD =====

    @GetMapping
    public List<Maintenance> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Maintenance getById(@PathVariable Long id) {
        return service.getById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    @PostMapping
    public Maintenance create(@RequestBody Maintenance maintenance) {
        return service.create(maintenance);
    }

    @PutMapping("/{id}")
    public Maintenance update(@PathVariable Long id,
                              @RequestBody Maintenance maintenance) {
        return service.update(id, maintenance);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // ===== CUSTOM ENDPOINTS =====

    @GetMapping("/vehicle/{vehicleId}")
    public List<Maintenance> byVehicle(@PathVariable Long vehicleId) {
        return service.getByVehicle(vehicleId);
    }

    @GetMapping("/status/{status}")
    public List<Maintenance> byStatus(@PathVariable MaintenanceStatus status) {
        return service.getByStatus(status);
    }

    @GetMapping("/type/{type}")
    public List<Maintenance> byType(@PathVariable String type) {
        return service.getByType(type);
    }

    @GetMapping("/date-range")
    public List<Maintenance> byDateRange(@RequestParam LocalDate start,
                                         @RequestParam LocalDate end) {
        return service.getByDateRange(start, end);
    }

    @GetMapping("/count/{status}")
    public long count(@PathVariable MaintenanceStatus status) {
        return service.countByStatus(status);
    }
}