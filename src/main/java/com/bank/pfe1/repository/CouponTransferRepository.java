package com.bank.pfe1.repository;

import com.bank.pfe1.entity.CouponTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CouponTransferRepository extends JpaRepository<CouponTransfer, Long> {
    List<CouponTransfer> findByToOrganizationId(Long organizationId);
    List<CouponTransfer> findByBatchId(Long batchId);
}
