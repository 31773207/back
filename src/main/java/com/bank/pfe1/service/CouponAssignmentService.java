package com.bank.pfe1.service;

import com.bank.pfe1.entity.CouponAssignment;
import com.bank.pfe1.entity.CouponStatus;
import com.bank.pfe1.entity.GasCoupon;
import com.bank.pfe1.repository.CouponAssignmentRepository;
import com.bank.pfe1.repository.GasCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponAssignmentService {

    private final CouponAssignmentRepository assignmentRepository;
    private final GasCouponRepository couponRepository;

    public List<CouponAssignment> getAll() {
        return assignmentRepository.findAll();
    }

    @Transactional
    public CouponAssignment useCoupons(Long assignmentId, int quantity) {
        CouponAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (assignment.getRemaining() < quantity) {
            throw new RuntimeException("Not enough coupons. Available: " + assignment.getRemaining());
        }

        // Check if batch is expired
        GasCoupon batch = assignment.getBatch();
        if (batch.getExpiryDate() != null && batch.getExpiryDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Cannot use expired coupons!");
        }

        assignment.setRemaining(assignment.getRemaining() - quantity);
        assignment.setUsedQuantity(assignment.getUsedQuantity() + quantity);

        if (assignment.getRemaining() == 0) {
            assignment.setStatus("USED");
            assignment.setUsedDate(LocalDate.now());
        } else {
            assignment.setStatus("PARTIALLY_USED");
        }

        // Update batch remaining quantity
        batch.setQuantityRemaining(batch.getQuantityRemaining() - quantity);
        if (batch.getQuantityRemaining() == 0) {
            batch.setStatus(CouponStatus.DEPLETED);
        }
        couponRepository.save(batch);

        return assignmentRepository.save(assignment);
    }
}