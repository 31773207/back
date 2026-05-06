package com.bank.pfe1.repository;

import com.bank.pfe1.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    // ❌ Remove this line - fields don't belong in interfaces
    // private final OrganizationRepository organizationRepository;
}