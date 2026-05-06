package com.bank.pfe1.repository;

import com.bank.pfe1.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUsernameOrderByTimestampDesc(String username);
    List<AuditLog> findByEntityTypeOrderByTimestampDesc(String entityType);
    List<AuditLog> findAllByOrderByTimestampDesc();
}