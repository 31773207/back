package com.bank.pfe1.repository;

import com.bank.pfe1.entity.CouponAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;  // ✅ ADD THIS IMPORT

public interface CouponAssignmentRepository extends JpaRepository<CouponAssignment, Long> {
    List<CouponAssignment> findByEmployeeId(Long employeeId);
    List<CouponAssignment> findByBatchId(Long batchId);
}