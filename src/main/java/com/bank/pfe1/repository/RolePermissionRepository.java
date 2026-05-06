package com.bank.pfe1.repository;

import com.bank.pfe1.entity.AppModule;
import com.bank.pfe1.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleId(Long roleId);
    Optional<RolePermission> findByRoleIdAndModule(Long roleId, AppModule module);
}