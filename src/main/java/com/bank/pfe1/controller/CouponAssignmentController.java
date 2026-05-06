package com.bank.pfe1.controller;

import com.bank.pfe1.entity.CouponAssignment;
import com.bank.pfe1.service.CouponAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;  // ✅ ADD THIS IMPORT
import java.util.List;

@RestController
@RequestMapping("/api/coupon-assignments")
@RequiredArgsConstructor
public class CouponAssignmentController {

    private final CouponAssignmentService service;

    @GetMapping
    public ResponseEntity<List<CouponAssignment>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/{id}/use")
    public ResponseEntity<CouponAssignment> useCoupons(
            @PathVariable Long id,
            @RequestParam int quantity) {
        return ResponseEntity.ok(service.useCoupons(id, quantity));
    }
}
