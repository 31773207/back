package com.bank.pfe1.controller;

import com.bank.pfe1.entity.Wilaya;
import com.bank.pfe1.repository.WilayaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/wilayas")
@RequiredArgsConstructor
public class WilayaController {

    private final WilayaRepository wilayaRepository;

    @GetMapping
    public ResponseEntity<List<Wilaya>> getAll() {
        return ResponseEntity.ok(wilayaRepository.findAll());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Wilaya>> getActive() {
        return ResponseEntity.ok(wilayaRepository.findByActive(true));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Wilaya>> search(@RequestParam String q) {
        return ResponseEntity.ok(wilayaRepository.findByNameContainingIgnoreCase(q));
    }

    @PostMapping
    public ResponseEntity<Wilaya> create(@RequestBody Wilaya wilaya) {
        if (wilayaRepository.existsByCode(wilaya.getCode())) {
            throw new RuntimeException("Wilaya code already exists!");
        }
        return ResponseEntity.ok(wilayaRepository.save(wilaya));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Wilaya> update(@PathVariable Long id, @RequestBody Wilaya updated) {
        Wilaya wilaya = wilayaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wilaya not found"));
        wilaya.setName(updated.getName());
        wilaya.setCode(updated.getCode());
        wilaya.setActive(updated.isActive());
        return ResponseEntity.ok(wilayaRepository.save(wilaya));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        wilayaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Wilaya> toggle(@PathVariable Long id) {
        Wilaya wilaya = wilayaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wilaya not found"));
        wilaya.setActive(!wilaya.isActive());
        return ResponseEntity.ok(wilayaRepository.save(wilaya));
    }
}