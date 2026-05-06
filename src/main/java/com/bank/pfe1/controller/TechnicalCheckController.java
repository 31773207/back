package com.bank.pfe1.controller;

import com.bank.pfe1.entity.TechnicalCheck;
import com.bank.pfe1.service.TechnicalCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/technical-checks")
public class TechnicalCheckController {

    @Autowired
    private TechnicalCheckService technicalCheckService;

    @GetMapping
    public List<TechnicalCheck> getAllChecks() {
        return technicalCheckService.getAllChecks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TechnicalCheck> getCheckById(@PathVariable Long id) {
        return ResponseEntity.ok(technicalCheckService.getCheckById(id));
    }

    @GetMapping("/vehicle/{vehicleId}")
    public List<TechnicalCheck> getChecksByVehicle(@PathVariable Long vehicleId) {
        return technicalCheckService.getChecksByVehicle(vehicleId);
    }

    @PostMapping
    public ResponseEntity<TechnicalCheck> createCheck(@RequestBody TechnicalCheck check) {
        return ResponseEntity.ok(technicalCheckService.createCheck(check));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TechnicalCheck> updateCheck(@PathVariable Long id, @RequestBody TechnicalCheck check) {
        return ResponseEntity.ok(technicalCheckService.updateCheck(id, check));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCheck(@PathVariable Long id) {
        technicalCheckService.deleteCheck(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/expiring-soon")
    public ResponseEntity<List<TechnicalCheck>> getExpiringSoon() {
        return ResponseEntity.ok(technicalCheckService.getExpiringSoon());
    }


    @GetMapping("/expired")
    public ResponseEntity<List<TechnicalCheck>> getExpired() {
        return ResponseEntity.ok(technicalCheckService.getExpired());
    }
}