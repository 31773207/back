package com.bank.pfe1.repository;

import com.bank.pfe1.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    boolean existsByLicenseNumber(String licenseNumber);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    // Find available drivers (not on mission)
    @Query("SELECT d FROM Driver d WHERE d.id NOT IN " +
            "(SELECT m.driver.id FROM Mission m WHERE m.status = 'IN_PROGRESS')")
    List<Driver> findAvailableDrivers();

    // Find drivers by organization
    List<Driver> findByOrganizationId(Long organizationId);
}
