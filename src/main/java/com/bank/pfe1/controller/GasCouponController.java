package com.bank.pfe1.controller;

import com.bank.pfe1.entity.*;
import com.bank.pfe1.service.GasCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/gas-coupons")
@RequiredArgsConstructor
public class GasCouponController {

    private final GasCouponService service;

    // 1. Buy coupons (create batch)
    @PostMapping("/buy")
    public ResponseEntity<GasCoupon> buyCoupons(
            @RequestParam int quantity,
            @RequestParam Double fuelAmount,
            @RequestParam LocalDate buyDate,
            @RequestParam LocalDate expiryDate,
            @RequestParam(required = false) Double totalCost) {
        return ResponseEntity.ok(service.buyCoupons(quantity, fuelAmount, buyDate, expiryDate));
    }

    // 2. Get all batches
    @GetMapping
    public ResponseEntity<List<GasCoupon>> getAll() {
        return ResponseEntity.ok(service.getAllCoupons());
    }

    // 3. Get available coupons (status = AVAILABLE)
    @GetMapping("/available")
    public ResponseEntity<List<GasCoupon>> getAvailable() {
        return ResponseEntity.ok(service.getAvailableCoupons());
    }

    // 4. Get total available quantity
    @GetMapping("/available-quantity")
    public ResponseEntity<Integer> getAvailableQuantity() {
        return ResponseEntity.ok(service.getTotalAvailableQuantity());
    }

    // 5. Assign coupons (decrease quantity)
    @PostMapping("/assign")
    public ResponseEntity<List<GasCoupon>> assignCoupons(@RequestParam int quantity) {
        return ResponseEntity.ok(service.assignCoupons(quantity));
    }

    // 6. Use coupons (decrease quantity)
    @PostMapping("/use")
    public ResponseEntity<List<GasCoupon>> useCoupons(@RequestParam int quantity) {
        return ResponseEntity.ok(service.useCoupons(quantity));
    }

    // 7. Mark expired coupons
    @PostMapping("/mark-expired")
    public ResponseEntity<Integer> markExpired() {
        return ResponseEntity.ok(service.markExpiredCoupons());
    }
    @PostMapping("/{id}/assign")
    public ResponseEntity<GasCoupon> assignCoupons(
            @PathVariable Long id,
            @RequestParam Long employeeId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(service.assignCoupons(id, employeeId, quantity));
    }
    // ✅ CORRECT
    @PostMapping("/{id}/transfer")
    public ResponseEntity<GasCoupon> transferCoupons(
            @PathVariable Long id,
            @RequestParam int quantity,
            @RequestParam Long organizationId) {
        return ResponseEntity.ok(service.transferCoupons(id, quantity, organizationId));
    }
}