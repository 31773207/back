package com.bank.pfe1.service;

import com.bank.pfe1.entity.*;
import com.bank.pfe1.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GasCouponService {

    private final GasCouponRepository couponRepository;
    private final CouponInventoryRepository inventoryRepository;
    private final EmployeeRepository employeeRepository;
    private final CouponAssignmentRepository assignmentRepository;
    private final NotificationService notificationService;
    private final OrganizationRepository organizationRepository;
    private final CouponTransferRepository transferRepository;

    // 1. Buy coupons
    @Transactional
    public GasCoupon buyCoupons(int quantity, Double fuelAmount, LocalDate buyDate, LocalDate expiryDate) {
        String batchNumber = "BATCH-" + buyDate.getYear() + "-" + System.currentTimeMillis();

        GasCoupon coupon = GasCoupon.builder()
                .batchNumber(batchNumber)
                .quantityBought(quantity)
                .quantityRemaining(quantity)
                .fuelAmount(fuelAmount)
                .buyDate(buyDate)  // ✅ Use the provided buyDate
                .expiryDate(expiryDate)
                .status(CouponStatus.AVAILABLE)
                .build();

        return couponRepository.save(coupon);
    }
    // 2. Get all
    public List<GasCoupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    // 3. Get available
    public List<GasCoupon> getAvailableCoupons() {
        return couponRepository.findByStatus(CouponStatus.AVAILABLE);
    }

    // 4. Get total available quantity
    public int getTotalAvailableQuantity() {
        return couponRepository.findByStatus(CouponStatus.AVAILABLE)
                .stream()
                .mapToInt(GasCoupon::getQuantityRemaining)
                .sum();
    }

    // 5. Assign coupons
    @Transactional
    public List<GasCoupon> assignCoupons(int quantity) {
        int available = getTotalAvailableQuantity();
        if (available < quantity) {
            throw new RuntimeException("Not enough coupons. Available: " + available);
        }

        List<GasCoupon> batches = couponRepository.findByStatus(CouponStatus.AVAILABLE);
        int remainingToAssign = quantity;
        List<GasCoupon> updatedBatches = new java.util.ArrayList<>();

        for (GasCoupon batch : batches) {
            if (remainingToAssign <= 0) break;

            int toTake = Math.min(remainingToAssign, batch.getQuantityRemaining());
            batch.setQuantityRemaining(batch.getQuantityRemaining() - toTake);

            if (batch.getQuantityRemaining() == 0) {
                batch.setStatus(CouponStatus.DEPLETED);
            }

            updatedBatches.add(couponRepository.save(batch));
            remainingToAssign -= toTake;
        }

        return updatedBatches;
    }

    // 6. Use coupons
    @Transactional
    public List<GasCoupon> useCoupons(int quantity) {
        return assignCoupons(quantity);
    }

    // 7. Mark expired
    @Transactional
    public int markExpiredCoupons() {
        List<GasCoupon> expired = couponRepository.findByStatusAndExpiryDateBefore(
                CouponStatus.AVAILABLE, LocalDate.now());

        expired.forEach(coupon -> coupon.setStatus(CouponStatus.EXPIRED));

        if (!expired.isEmpty()) {
            couponRepository.saveAll(expired);
        }

        return expired.size();
    }
    @Transactional
    public GasCoupon assignCoupons(Long batchId, Long employeeId, int quantity) {
        GasCoupon batch = couponRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        if (batch.getExpiryDate() != null && batch.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot assign expired coupons!");
        }

        if (batch.getQuantityRemaining() < quantity) {
            throw new RuntimeException("Not enough coupons. Available: " + batch.getQuantityRemaining());
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Create assignment record
        CouponAssignment assignment = CouponAssignment.builder()
                .batch(batch)
                .employee(employee)
                .quantity(quantity)
                .remaining(quantity)
                .assignedDate(LocalDate.now())
                .status("ASSIGNED")
                .build();

        assignmentRepository.save(assignment);

        // Update batch quantity
        batch.setQuantityRemaining(batch.getQuantityRemaining() - quantity);
        if (batch.getQuantityRemaining() == 0) {
            batch.setStatus(CouponStatus.DEPLETED);
        }

        couponRepository.save(batch);
        updateInventory();

        return batch;
    }
    private void updateInventory() {
        long total = couponRepository.count();
        long available = couponRepository.countByStatus(CouponStatus.AVAILABLE);
        long assigned = couponRepository.countByStatus(CouponStatus.ASSIGNED);
        long used = couponRepository.countByStatus(CouponStatus.USED);
        long expired = couponRepository.countByStatus(CouponStatus.EXPIRED);

        CouponInventory inventory = inventoryRepository.findById(1L)
                .orElse(new CouponInventory());

        inventory.setTotalCoupons((int) total);
        inventory.setAvailableCoupons((int) available);
        inventory.setAssignedCoupons((int) assigned);
        inventory.setUsedCoupons((int) used);
        inventory.setExpiredCoupons((int) expired);
        inventory.setLastUpdated(LocalDate.now());

        inventoryRepository.save(inventory);
    }

    @Transactional
    public GasCoupon transferCoupons(Long batchId, int quantity, Long toOrganizationId) {
        GasCoupon batch = couponRepository.findById(batchId)
                .orElseThrow(() -> new RuntimeException("Batch not found"));

        if (batch.getQuantityRemaining() < quantity) {
            throw new RuntimeException("Not enough coupons");
        }

        Organization toOrg = organizationRepository.findById(toOrganizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        // ✅ Create and save transfer record
        CouponTransfer transfer = CouponTransfer.builder()
                .batch(batch)
                .quantity(quantity)
                .transferDate(LocalDate.now())
                .toOrganization(toOrg)
                .build();

        transferRepository.save(transfer);  // ✅ Save to database

        // Update batch quantity
        batch.setQuantityRemaining(batch.getQuantityRemaining() - quantity);
        if (batch.getQuantityRemaining() == 0) {
            batch.setStatus(CouponStatus.DEPLETED);
        }

        return couponRepository.save(batch);
    }
}

