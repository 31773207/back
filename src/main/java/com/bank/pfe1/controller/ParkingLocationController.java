package com.bank.pfe1.controller;

import com.bank.pfe1.entity.ParkingLocation;
import com.bank.pfe1.repository.ParkingLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/parking-locations")
@RequiredArgsConstructor
public class ParkingLocationController {

    private final ParkingLocationRepository parkingLocationRepository;

    @GetMapping
    public ResponseEntity<List<ParkingLocation>> getAll() {
        return ResponseEntity.ok(parkingLocationRepository.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ParkingLocation>> getActive() {
        return ResponseEntity.ok(parkingLocationRepository.findByActive(true));
    }

    @GetMapping("/wilaya/{wilayaId}")
    public ResponseEntity<List<ParkingLocation>> getByWilaya(@PathVariable Long wilayaId) {
        return ResponseEntity.ok(parkingLocationRepository.findByWilayaId(wilayaId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ParkingLocation>> search(@RequestParam String q) {
        return ResponseEntity.ok(parkingLocationRepository.findByNameContainingIgnoreCase(q));
    }

    @PostMapping
    public ResponseEntity<ParkingLocation> create(@RequestBody ParkingLocation location) {
        return ResponseEntity.ok(parkingLocationRepository.save(location));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingLocation> update(@PathVariable Long id, @RequestBody ParkingLocation updated) {
        ParkingLocation location = parkingLocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        location.setName(updated.getName());
        location.setAddress(updated.getAddress());
        location.setWilaya(updated.getWilaya());
        location.setActive(updated.isActive());
        return ResponseEntity.ok(parkingLocationRepository.save(location));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        parkingLocationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}