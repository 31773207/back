package com.bank.pfe1.service;

import com.bank.pfe1.entity.Driver;
import com.bank.pfe1.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver getDriverById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
    }

    public List<Driver> getAvailableDrivers() {
        return driverRepository.findAvailableDrivers();
    }

    @Transactional
    public Driver createDriver(Driver driver) {
        driver.setEmployeeType("DRIVER");

        if (driver.getLicenseNumber() != null) {
            if (driverRepository.existsByLicenseNumber(driver.getLicenseNumber()))
                throw new RuntimeException("License number already exists!");
        }
        if (driverRepository.existsByEmail(driver.getEmail()))
            throw new RuntimeException("Email already exists!");
        if (driverRepository.existsByPhone(driver.getPhone()))
            throw new RuntimeException("Phone already exists!");

        return driverRepository.save(driver);
    }

    @Transactional
    public Driver updateDriver(Long id, Driver updated) {
        Driver driver = getDriverById(id);
        driver.setFirstName(updated.getFirstName());
        driver.setLastName(updated.getLastName());
        driver.setPhone(updated.getPhone());
        driver.setEmail(updated.getEmail());
        driver.setAddress(updated.getAddress());
        driver.setDateOfBirth(updated.getDateOfBirth());
        driver.setLicenseNumber(updated.getLicenseNumber());
        driver.setLicenseExpiry(updated.getLicenseExpiry());
        driver.setOrganization(updated.getOrganization());

        return driverRepository.save(driver);
    }

    @Transactional
    public void deleteDriver(Long id) {
        driverRepository.deleteById(id);
    }
}