package com.bank.pfe1.repository;

import com.bank.pfe1.entity.GasCoupon;
import com.bank.pfe1.entity.CouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface GasCouponRepository extends JpaRepository<GasCoupon, Long> {

    List<GasCoupon> findByStatus(CouponStatus status);

    List<GasCoupon> findByStatusAndExpiryDateBefore(CouponStatus status, LocalDate date);

    List<GasCoupon> findByQuantityRemainingGreaterThan(int quantity);

    // ✅ ADD THIS METHOD
    long countByStatus(CouponStatus status);
}