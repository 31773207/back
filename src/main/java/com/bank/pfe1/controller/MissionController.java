package com.bank.pfe1.controller;

import com.bank.pfe1.entity.*;
import com.bank.pfe1.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MissionController {

    private final MissionService missionService;

    // ==================== MISSION ENDPOINTS ====================

    // Get all missions
    @GetMapping
    public ResponseEntity<List<Mission>> getAllMissions() {
        return ResponseEntity.ok(missionService.getAllMissions());
    }

    // Get mission by ID
    @GetMapping("/{id}")
    public ResponseEntity<Mission> getMissionById(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.getMissionById(id));
    }

    // Get missions by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Mission>> getMissionsByStatus(@PathVariable MissionStatus status) {
        return ResponseEntity.ok(missionService.getMissionsByStatus(status));
    }

    // Get missions by driver
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Mission>> getMissionsByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(missionService.getMissionsByDriver(driverId));
    }

    // Get available drivers (not on mission)
    @GetMapping("/available-drivers")
    public ResponseEntity<List<Driver>> getAvailableDrivers() {
        return ResponseEntity.ok(missionService.getAvailableDrivers());
    }

    // ✅ NEW: Get available drivers ordered by least missions (fair distribution)
    @GetMapping("/available-drivers/ordered")
    public ResponseEntity<List<Driver>> getAvailableDriversOrdered() {
        return ResponseEntity.ok(missionService.getAvailableDriversOrderedByLeastMissions());
    }

    // Create new mission
    @PostMapping
    public ResponseEntity<Mission> createMission(@RequestBody Mission mission) {
        return ResponseEntity.ok(missionService.createMission(mission));
    }

    // Update mission
    @PutMapping("/{id}")
    public ResponseEntity<Mission> updateMission(@PathVariable Long id, @RequestBody Mission mission) {
        return ResponseEntity.ok(missionService.updateMission(id, mission));
    }

    // Start mission
    @PatchMapping("/{id}/start")
    public ResponseEntity<Mission> startMission(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.startMission(id));
    }

    // Complete mission with odometer reading
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Mission> completeMission(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> payload) {
        Double finalKilometrage = payload != null && payload.containsKey("finalKilometrage")
                ? ((Number) payload.get("finalKilometrage")).doubleValue()
                : null;
        return ResponseEntity.ok(missionService.completeMission(id, finalKilometrage));
    }

    // Cancel mission
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Mission> cancelMission(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.cancelMission(id));
    }

    // Delete mission
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        missionService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== MISSION TIME ENDPOINTS ====================

    // Get all mission times for a mission
    @GetMapping("/{missionId}/times")
    public ResponseEntity<List<MissionTime>> getTimesByMission(@PathVariable Long missionId) {
        return ResponseEntity.ok(missionService.getTimesByMission(missionId));
    }

    // Add mission time
    @PostMapping("/times")
    public ResponseEntity<MissionTime> addMissionTime(@RequestBody MissionTime missionTime) {
        return ResponseEntity.ok(missionService.addMissionTime(missionTime));
    }

    // Delete mission time
    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteMissionTime(@PathVariable Long id) {
        missionService.deleteMissionTime(id);
        return ResponseEntity.noContent().build();
    }
}