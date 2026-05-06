package com.bank.pfe1.repository;

import com.bank.pfe1.entity.CouponInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponInventoryRepository extends JpaRepository<CouponInventory, Long> {
}