package com.bank.pfe1.controller;

import com.bank.pfe1.entity.CouponTransfer;
import com.bank.pfe1.repository.CouponTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/coupon-transfers")
@RequiredArgsConstructor
public class CouponTransferController {

    private final CouponTransferRepository transferRepository;

    @GetMapping
    public ResponseEntity<List<CouponTransfer>> getAllTransfers() {
        return ResponseEntity.ok(transferRepository.findAll());
    }
}