/*package com.bank.pfe1.service;

import com.bank.pfe1.entity.*;
import com.bank.pfe1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class TechnicalCheckService {

    @Autowired
    private TechnicalCheckRepository technicalCheckRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<TechnicalCheck> getAllChecks() {
        return technicalCheckRepository.findAll();
    }

    public TechnicalCheck getCheckById(Long id) {
        return technicalCheckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TechnicalCheck not found with id: " + id));
    }

    public List<TechnicalCheck> getChecksByVehicle(Long vehicleId) {
        return technicalCheckRepository.findByVehicleId(vehicleId);
    }

    public TechnicalCheck createCheck(TechnicalCheck check) {
        Vehicle vehicle = vehicleRepository.findById(check.getVehicle().getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        check.setVehicle(vehicle);
        check.setStatus(TechnicalCheckStatus.VALID);
        return technicalCheckRepository.save(check);
    }

    public TechnicalCheck updateCheck(Long id, TechnicalCheck updated) {
        TechnicalCheck check = getCheckById(id);
        check.setCheckDate(updated.getCheckDate());
        check.setExpiryDate(updated.getExpiryDate());
        check.setCenter(updated.getCenter());
        check.setNotes(updated.getNotes());
        check.setStatus(updated.getStatus());
        return technicalCheckRepository.save(check);
    }

    public void deleteCheck(Long id) {
        technicalCheckRepository.deleteById(id);
    }


    public List<TechnicalCheck> getExpiringSoon() {
        LocalDate today = LocalDate.now();
        LocalDate in15Days = today.plusDays(15);
        return technicalCheckRepository.findByExpiryDateBetween(today, in15Days);
    }


    public List<TechnicalCheck> getExpired() {
        return technicalCheckRepository.findByExpiryDateBefore(LocalDate.now());
    }


    @Scheduled(cron = "0 0 0 * * *")
    public void autoUpdateExpiredStatuses() {
        List<TechnicalCheck> expired = technicalCheckRepository
                .findByExpiryDateBefore(LocalDate.now());

        for (TechnicalCheck check : expired) {
            if (check.getStatus() != TechnicalCheckStatus.EXPIRED) {
                check.setStatus(TechnicalCheckStatus.EXPIRED);
                technicalCheckRepository.save(check);
            }
        }
    }
}*/
package com.bank.pfe1.service;

import com.bank.pfe1.entity.*;
import com.bank.pfe1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class TechnicalCheckService {

    @Autowired
    private TechnicalCheckRepository technicalCheckRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    // ── helper: calculate status from expiryDate ──────────
    private TechnicalCheckStatus calculateStatus(LocalDate expiryDate) {
        if (expiryDate == null) return TechnicalCheckStatus.VALID;
        return expiryDate.isBefore(LocalDate.now())
                ? TechnicalCheckStatus.EXPIRED
                : TechnicalCheckStatus.VALID;
    }

    public List<TechnicalCheck> getAllChecks() {
        return technicalCheckRepository.findAll();
    }

    public TechnicalCheck getCheckById(Long id) {
        return technicalCheckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TechnicalCheck not found with id: " + id));
    }

    public List<TechnicalCheck> getChecksByVehicle(Long vehicleId) {
        return technicalCheckRepository.findByVehicleId(vehicleId);
    }

    // ── FIX 1: auto-calculate status based on expiryDate ──
    public TechnicalCheck createCheck(TechnicalCheck check) {
        Vehicle vehicle = vehicleRepository.findById(check.getVehicle().getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        check.setVehicle(vehicle);
        check.setStatus(calculateStatus(check.getExpiryDate())); // ← fixed
        return technicalCheckRepository.save(check);
    }

    // ── FIX 2: recalculate status on update ───────────────
    public TechnicalCheck updateCheck(Long id, TechnicalCheck updated) {
        TechnicalCheck check = getCheckById(id);
        check.setCheckDate(updated.getCheckDate());
        check.setExpiryDate(updated.getExpiryDate());
        check.setCenter(updated.getCenter());
        check.setNotes(updated.getNotes());
        // only allow FAILED to be set manually, otherwise auto-calculate
        if (updated.getStatus() == TechnicalCheckStatus.FAILED) {
            check.setStatus(TechnicalCheckStatus.FAILED);
        } else {
            check.setStatus(calculateStatus(updated.getExpiryDate())); // ← fixed
        }
        return technicalCheckRepository.save(check);
    }

    public void deleteCheck(Long id) {
        technicalCheckRepository.deleteById(id);
    }

    // ── FIX 3: filter by VALID status only ────────────────
    public List<TechnicalCheck> getExpiringSoon() {
        LocalDate today = LocalDate.now();
        LocalDate in15Days = today.plusDays(15);
        return technicalCheckRepository
                .findByStatusAndExpiryDateBetween(
                        TechnicalCheckStatus.VALID, today, in15Days // ← fixed
                );
    }

    public List<TechnicalCheck> getExpired() {
        return technicalCheckRepository.findByExpiryDateBefore(LocalDate.now());
    }

    // ── Scheduler: runs every midnight ───────────────────
    @Scheduled(cron = "0 0 0 * * *")
    public void autoUpdateExpiredStatuses() {
        List<TechnicalCheck> expired = technicalCheckRepository
                .findByExpiryDateBefore(LocalDate.now());

        for (TechnicalCheck check : expired) {
            if (check.getStatus() != TechnicalCheckStatus.EXPIRED) {
                check.setStatus(TechnicalCheckStatus.EXPIRED);
                technicalCheckRepository.save(check);
            }
        }
    }
}