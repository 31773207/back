package com.bank.pfe1.controller;

import com.bank.pfe1.entity.*;
import com.bank.pfe1.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final VehicleRepository vehicleRepository;
    private final MissionRepository missionRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final GasCouponRepository gasCouponRepository;
    private final TechnicalCheckRepository technicalCheckRepository;
    private final DriverRepository driverRepository;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // VEHICLE STATS
        stats.put("totalVehicles",    vehicleRepository.count());
        stats.put("activeVehicles",   vehicleRepository.findByStatus(VehicleStatus.AVAILABLE).size());
        stats.put("assignedVehicles", vehicleRepository.findByStatus(VehicleStatus.ASSIGNED).size());
        stats.put("inMission",        vehicleRepository.findByStatus(VehicleStatus.IN_MISSION).size());
        stats.put("inRevision",       vehicleRepository.findByStatus(VehicleStatus.IN_REVISION).size());
        stats.put("breakdown",        vehicleRepository.findByStatus(VehicleStatus.BREAKDOWN).size());
        stats.put("reformed",         vehicleRepository.findByStatus(VehicleStatus.REFORMED).size());

        // DRIVER STATS
        stats.put("totalDrivers", driverRepository.count());

        // MISSION STATS
        stats.put("totalMissions",      missionRepository.count());
        stats.put("missionsPlanned",    missionRepository.findByStatus(MissionStatus.PLANNED).size());
        stats.put("missionsInProgress", missionRepository.findByStatus(MissionStatus.IN_PROGRESS).size());
        stats.put("missionsCompleted",  missionRepository.findByStatus(MissionStatus.COMPLETED).size());
        stats.put("missionsCancelled",  missionRepository.findByStatus(MissionStatus.CANCELLED).size());

        // MISSIONS THIS MONTH
        LocalDate firstDayThisMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastDayThisMonth = firstDayThisMonth.plusMonths(1).minusDays(1);
        List<Mission> missionsThisMonth = missionRepository.findByStartDateBetween(firstDayThisMonth, lastDayThisMonth);
        stats.put("missionsThisMonth", missionsThisMonth.size());

        // MISSIONS LAST MONTH
        LocalDate firstDayLastMonth = firstDayThisMonth.minusMonths(1);
        LocalDate lastDayLastMonth = firstDayThisMonth.minusDays(1);
        List<Mission> missionsLastMonth = missionRepository.findByStartDateBetween(firstDayLastMonth, lastDayLastMonth);
        stats.put("missionsLastMonth", missionsLastMonth.size());

        // MAINTENANCE STATS
        stats.put("totalMaintenance", maintenanceRepository.count());
        stats.put("openMaintenance",  maintenanceRepository.countByStatus(MaintenanceStatus.IN_PROGRESS));
        stats.put("doneMaintenance",  maintenanceRepository.countByStatus(MaintenanceStatus.COMPLETED));

        // TOTAL MAINTENANCE COST
        List<Maintenance> allMaintenance = maintenanceRepository.findAll();
        BigDecimal totalCost = allMaintenance.stream()
                .map(Maintenance::getCost)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));  // ✅ FIXED
        stats.put("totalMaintenanceCost", totalCost);

        // MAINTENANCE COST THIS MONTH
        List<Maintenance> maintenanceThisMonth = maintenanceRepository.findByStartDateBetween(firstDayThisMonth, lastDayThisMonth);
        BigDecimal costThisMonth = maintenanceThisMonth.stream()
                .map(Maintenance::getCost)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));  // ✅ FIXED
        stats.put("maintenanceCostThisMonth", costThisMonth);

        // GAS COUPON STATS
        stats.put("couponsAvailable",   gasCouponRepository.countByStatus(CouponStatus.AVAILABLE));
        stats.put("couponsAssigned",    gasCouponRepository.countByStatus(CouponStatus.ASSIGNED));
        stats.put("couponsUsed",        gasCouponRepository.countByStatus(CouponStatus.USED));
        stats.put("couponsTransferred", gasCouponRepository.countByStatus(CouponStatus.TRANSFERRED));

        // TOTAL FUEL USED
        List<GasCoupon> usedCoupons = gasCouponRepository.findByStatus(CouponStatus.USED);
        BigDecimal totalFuel = usedCoupons.stream()
                .map(GasCoupon::getFuelAmount)
                .map(amount -> amount != null ? BigDecimal.valueOf(amount) : BigDecimal.ZERO)  // ✅ Double to BigDecimal conversion
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
        stats.put("totalFuelUsed", totalFuel);

        // TECHNICAL CHECK STATS
        stats.put("validChecks",   technicalCheckRepository.findByStatus(TechnicalCheckStatus.VALID).size());
        stats.put("expiredChecks", technicalCheckRepository.findByStatus(TechnicalCheckStatus.EXPIRED).size());
        stats.put("expiringSoon",  technicalCheckRepository
                .findByExpiryDateBetween(LocalDate.now(), LocalDate.now().plusDays(15)).size());

        // VEHICLE UTILIZATION RATE
        long total = vehicleRepository.count();
        long inUse = vehicleRepository.findByStatus(VehicleStatus.IN_MISSION).size()
                + vehicleRepository.findByStatus(VehicleStatus.ASSIGNED).size();
        double utilizationRate = total > 0 ? (double) inUse / total * 100 : 0;
        stats.put("vehicleUtilizationRate", Math.round(utilizationRate));

        return ResponseEntity.ok(stats);
    }
}