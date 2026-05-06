package com.bank.pfe1.scheduler;

import com.bank.pfe1.entity.*;
import com.bank.pfe1.repository.*;
import com.bank.pfe1.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MaintenanceScheduler {

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;
    private final NotificationService notificationService;

    /**
     * Runs every day at 7:00 AM
     * ONLY sends notifications - NO auto-creation of maintenance records
     */
    @Scheduled(cron = "0 0 7 * * *")
    @Transactional
    public void checkAndNotifyMaintenanceNeeds() {
        System.out.println("🔍 Running maintenance check at " + LocalDateTime.now());

        List<Vehicle> allVehicles = vehicleRepository.findAll();
        int notificationCount = 0;

        for (Vehicle vehicle : allVehicles) {
            boolean hasActiveMaintenance = maintenanceRepository.findByVehicleId(vehicle.getId()).stream()
                    .anyMatch(m -> m.getStatus() != MaintenanceStatus.COMPLETED);

            if (hasActiveMaintenance) continue;

            String maintenanceType = determineMaintenanceType(vehicle);
            if (maintenanceType != null) {
                String reason = getMaintenanceReason(vehicle, maintenanceType);

                // ✅ ONLY send notification - NO auto-create
                notificationService.createNotification(
                        "⚠️ Maintenance Required",
                        "Vehicle " + vehicle.getPlateNumber() + " needs " + maintenanceType +
                                ". " + reason + " - Please schedule maintenance.",
                        NotificationType.MAINTENANCE,
                        vehicle.getId(),
                        "/maintenance?action=create&vehicleId=" + vehicle.getId()
                );
                notificationCount++;
            }
        }

        System.out.println("✅ Sent " + notificationCount + " maintenance reminders");
    }
    /**
     * Runs every day at 7:30 AM
     * Auto-completes maintenance when end date has passed
     */
    @Scheduled(cron = "0 30 7 * * *")
    @Transactional
    public void autoCompleteOverdueMaintenance() {
        System.out.println("🔧 Auto-completing overdue maintenance at " + LocalDateTime.now());

        List<Maintenance> allMaintenance = maintenanceRepository.findAll();
        int completedCount = 0;

        for (Maintenance m : allMaintenance) {
            if (m.getEndDate() != null &&
                    (m.getEndDate().isBefore(LocalDate.now()) || m.getEndDate().equals(LocalDate.now())) &&
                    m.getStatus() != MaintenanceStatus.COMPLETED) {

                m.setStatus(MaintenanceStatus.COMPLETED);
                maintenanceRepository.save(m);
                completedCount++;

                // Update vehicle records
                updateVehicleMaintenanceRecords(m);

                // Send notification
                notificationService.createNotification(
                        "Maintenance Auto-Completed",
                        "Maintenance " + m.getMaintenanceType() + " for vehicle " + m.getVehicle().getPlateNumber() + " has been auto-completed",
                        NotificationType.MAINTENANCE,
                        m.getVehicle().getId(),
                        "/maintenance"
                );

                // Check if vehicle has all maintenance completed
                checkAndUpdateVehicleStatus(m.getVehicle().getId());
            }
        }

        if (completedCount > 0) {
            System.out.println("✅ Auto-completed " + completedCount + " maintenance records at 7:30 AM");
        }
    }

    /**
     * Sends notification for maintenance needed (NO auto-creation)
     */
    private void sendMaintenanceNotification(Vehicle vehicle, String maintenanceType, String reason) {
        notificationService.createNotification(
                "⚠️ Maintenance Required",
                "Vehicle " + vehicle.getPlateNumber() + " needs " + formatMaintenanceType(maintenanceType) +
                        ". " + reason + " - Please schedule maintenance manually.",
                NotificationType.MAINTENANCE,
                vehicle.getId(),
                "/maintenance?action=create&vehicleId=" + vehicle.getId() + "&type=" + maintenanceType
        );
    }

    /**
     * Formats maintenance type for better readability in notifications
     */
    private String formatMaintenanceType(String type) {
        switch (type) {
            case "OIL_CHANGE": return "Oil Change";
            case "AIR_FILTER_REPLACEMENT": return "Air Filter Replacement";
            case "CABIN_FILTER_REPLACEMENT": return "Cabin Filter Replacement";
            case "BRAKE_INSPECTION": return "Brake Inspection";
            case "BRAKE_CHECK": return "Brake Check";
            case "SPARK_PLUG_REPLACEMENT": return "Spark Plug Replacement";
            case "FUEL_FILTER_REPLACEMENT": return "Fuel Filter Replacement";
            case "TIMING_BELT_REPLACEMENT": return "Timing Belt Replacement";
            case "TRANSMISSION_SERVICE": return "Transmission Service";
            case "FULL_INSPECTION": return "Full Inspection";
            case "TIRE_PRESSURE_CHECK": return "Tire Pressure Check";
            case "FLUID_LEVELS_CHECK": return "Fluid Levels Check";
            case "GENERAL_INSPECTION": return "General Inspection";
            case "FULL_SERVICE": return "Full Service";
            case "BRAKE_FLUID_REPLACEMENT": return "Brake Fluid Replacement";
            case "COOLANT_REPLACEMENT": return "Coolant Replacement";
            default: return type;
        }
    }

    // ===== MAIN SWITCH CASE METHOD =====

    /**
     * Determines what maintenance is needed based on vehicle conditions
     * Returns maintenance type or null if none needed
     */
    private String determineMaintenanceType(Vehicle vehicle) {
        Integer currentMileage = vehicle.getKilometrage() != null
                ? vehicle.getKilometrage().intValue()
                : null;
        LocalDate today = LocalDate.now();

        if (currentMileage == null) return null;

        // Get the condition code based on what's due
        int conditionCode = getConditionCode(vehicle, currentMileage, today);

        // SWITCH CASE for all maintenance types
        switch (conditionCode) {
            // ===== MILEAGE CONDITIONS (1-9) =====
            case 1:
                return "OIL_CHANGE";
            case 2:
                return "AIR_FILTER_REPLACEMENT";
            case 3:
                return "CABIN_FILTER_REPLACEMENT";
            case 4:
                return "BRAKE_INSPECTION";
            case 5:
                return "SPARK_PLUG_REPLACEMENT";
            case 6:
                return "FUEL_FILTER_REPLACEMENT";
            case 7:
                return "TIMING_BELT_REPLACEMENT";
            case 8:
                return "TRANSMISSION_SERVICE";
            case 9:
                return "FULL_INSPECTION";

            // ===== TIME CONDITIONS (10-17) =====
            case 10:
                return "TIRE_PRESSURE_CHECK";
            case 11:
                return "FLUID_LEVELS_CHECK";
            case 12:
                return "GENERAL_INSPECTION";
            case 13:
                return "OIL_CHANGE";  // Time-based oil change
            case 14:
                return "BRAKE_CHECK";
            case 15:
                return "FULL_SERVICE";
            case 16:
                return "BRAKE_FLUID_REPLACEMENT";
            case 17:
                return "COOLANT_REPLACEMENT";

            default:
                return null;
        }
    }

    /**
     * Calculates condition code based on what maintenance is due
     */
    private int getConditionCode(Vehicle vehicle, int currentMileage, LocalDate today) {

        // ===== MILEAGE CONDITIONS =====

        // Case 1: Oil Change (5,000 - 10,000 km)
        if (vehicle.getLastOilChangeMileage() != null) {
            int kmSince = currentMileage - vehicle.getLastOilChangeMileage();
            int threshold = "SYNTHETIC".equals(vehicle.getOilType()) ? 10000 : 5000;
            if (kmSince >= threshold) return 1;
        } else if (currentMileage >= 5000) {
            return 1;
        }

        // Case 2: Air Filter (15,000 km)
        if (vehicle.getLastAirFilterMileage() != null && vehicle.getLastAirFilterMileage() > 0) {
            if (currentMileage - vehicle.getLastAirFilterMileage() >= 15000) return 2;
        } else if (currentMileage >= 15000) {
            return 2;
        }

        // Case 3: Cabin Filter (15,000 km)
        if (vehicle.getLastCabinFilterMileage() != null && vehicle.getLastCabinFilterMileage() > 0) {
            if (currentMileage - vehicle.getLastCabinFilterMileage() >= 15000) return 3;
        } else if (currentMileage >= 15000) {
            return 3;
        }

        // Case 4: Brake Inspection (20,000 km)
        if (vehicle.getLastBrakeInspectionMileage() != null && vehicle.getLastBrakeInspectionMileage() > 0) {
            if (currentMileage - vehicle.getLastBrakeInspectionMileage() >= 20000) return 4;
        } else if (currentMileage >= 20000) {
            return 4;
        }

        // Case 5: Spark Plugs (40,000 km) - Petrol only
        if ("PETROL".equals(vehicle.getEngineType())) {
            if (vehicle.getLastSparkPlugMileage() != null && vehicle.getLastSparkPlugMileage() > 0) {
                if (currentMileage - vehicle.getLastSparkPlugMileage() >= 40000) return 5;
            } else if (currentMileage >= 40000) {
                return 5;
            }
        }

        // Case 6: Fuel Filter (40,000 km)
        if (vehicle.getLastFuelFilterMileage() != null && vehicle.getLastFuelFilterMileage() > 0) {
            if (currentMileage - vehicle.getLastFuelFilterMileage() >= 40000) return 6;
        } else if (currentMileage >= 40000) {
            return 6;
        }

        // Case 7: Timing Belt (60,000 km)
        if (vehicle.getLastTimingBeltMileage() != null && vehicle.getLastTimingBeltMileage() > 0) {
            if (currentMileage - vehicle.getLastTimingBeltMileage() >= 60000) return 7;
        } else if (currentMileage >= 60000) {
            return 7;
        }

        // Case 8: Transmission Oil (60,000 km)
        if (vehicle.getLastTransmissionOilMileage() != null && vehicle.getLastTransmissionOilMileage() > 0) {
            if (currentMileage - vehicle.getLastTransmissionOilMileage() >= 60000) return 8;
        } else if (currentMileage >= 60000) {
            return 8;
        }

        // Case 9: Full Inspection (Every 10,000 km)
        if (vehicle.getLastFullInspectionMileage() != null && vehicle.getLastFullInspectionMileage() > 0) {
            if (currentMileage - vehicle.getLastFullInspectionMileage() >= 10000) return 9;
        } else if (currentMileage >= 10000) {
            return 9;
        }

        // ===== TIME CONDITIONS =====

        // Case 10: Tire Pressure Check (1 month)
        if (vehicle.getLastTirePressureCheckDate() != null) {
            long monthsSince = java.time.temporal.ChronoUnit.MONTHS.between(vehicle.getLastTirePressureCheckDate(), today);
            if (monthsSince >= 1) return 10;
        }

        // Case 11: Fluid Levels Check (1 month)
        if (vehicle.getLastFluidLevelsCheckDate() != null) {
            long monthsSince = java.time.temporal.ChronoUnit.MONTHS.between(vehicle.getLastFluidLevelsCheckDate(), today);
            if (monthsSince >= 1) return 11;
        }

        // Case 12: General Inspection (3 months)
        if (vehicle.getLastGeneralInspectionDate() != null) {
            long monthsSince = java.time.temporal.ChronoUnit.MONTHS.between(vehicle.getLastGeneralInspectionDate(), today);
            if (monthsSince >= 3) return 12;
        }

        // Case 13: Oil Change by Time (6 months)
        if (vehicle.getLastOilChangeDate() != null) {
            long monthsSince = java.time.temporal.ChronoUnit.MONTHS.between(vehicle.getLastOilChangeDate(), today);
            if (monthsSince >= 6) return 13;
        }

        // Case 14: Brake Check (6 months)
        if (vehicle.getLastBrakeInspectionDate() != null) {
            long monthsSince = java.time.temporal.ChronoUnit.MONTHS.between(vehicle.getLastBrakeInspectionDate(), today);
            if (monthsSince >= 6) return 14;
        }

        // Case 15: Full Service (12 months)
        if (vehicle.getLastBatteryCheckDate() != null) {
            long monthsSince = java.time.temporal.ChronoUnit.MONTHS.between(vehicle.getLastBatteryCheckDate(), today);
            if (monthsSince >= 12) return 15;
        }

        // Case 16: Brake Fluid Replacement (2 years)
        if (vehicle.getLastBrakeFluidDate() != null) {
            long yearsSince = java.time.temporal.ChronoUnit.YEARS.between(vehicle.getLastBrakeFluidDate(), today);
            if (yearsSince >= 2) return 16;
        }

        // Case 17: Coolant Replacement (2 years)
        if (vehicle.getLastCoolantDate() != null) {
            long yearsSince = java.time.temporal.ChronoUnit.YEARS.between(vehicle.getLastCoolantDate(), today);
            if (yearsSince >= 2) return 17;
        }

        return 0;
    }
    /**
     * Generates description based on maintenance type using SWITCH CASE
     */
    private String getMaintenanceReason(Vehicle vehicle, String maintenanceType) {
        Integer currentMileage = vehicle.getKilometrage() != null ? vehicle.getKilometrage().intValue() : 0;
        LocalDate today = LocalDate.now();

        switch (maintenanceType) {
            case "OIL_CHANGE":
                if (vehicle.getLastOilChangeMileage() != null) {
                    int kmSince = currentMileage - vehicle.getLastOilChangeMileage();
                    return kmSince + " km since last oil change";
                } else if (vehicle.getLastOilChangeDate() != null) {
                    long monthsSince = java.time.temporal.ChronoUnit.MONTHS.between(vehicle.getLastOilChangeDate(), today);
                    return monthsSince + " months since last oil change";
                } else {
                    return "First oil change needed at " + currentMileage + " km";
                }

            case "AIR_FILTER_REPLACEMENT":
                int airKm = currentMileage - (vehicle.getLastAirFilterMileage() != null ? vehicle.getLastAirFilterMileage() : 0);
                return airKm + " km since last air filter change";

            case "CABIN_FILTER_REPLACEMENT":
                int cabinKm = currentMileage - (vehicle.getLastCabinFilterMileage() != null ? vehicle.getLastCabinFilterMileage() : 0);
                return cabinKm + " km since last cabin filter change";

            case "BRAKE_INSPECTION":
                int brakeKm = currentMileage - (vehicle.getLastBrakeInspectionMileage() != null ? vehicle.getLastBrakeInspectionMileage() : 0);
                return brakeKm + " km since last brake inspection";

            case "SPARK_PLUG_REPLACEMENT":
                int sparkKm = currentMileage - (vehicle.getLastSparkPlugMileage() != null ? vehicle.getLastSparkPlugMileage() : 0);
                return sparkKm + " km since last spark plug change";

            case "FUEL_FILTER_REPLACEMENT":
                int fuelKm = currentMileage - (vehicle.getLastFuelFilterMileage() != null ? vehicle.getLastFuelFilterMileage() : 0);
                return fuelKm + " km since last fuel filter change";

            case "TIMING_BELT_REPLACEMENT":
                int beltKm = currentMileage - (vehicle.getLastTimingBeltMileage() != null ? vehicle.getLastTimingBeltMileage() : 0);
                return beltKm + " km since last timing belt change";

            case "TRANSMISSION_SERVICE":
                int transKm = currentMileage - (vehicle.getLastTransmissionOilMileage() != null ? vehicle.getLastTransmissionOilMileage() : 0);
                return transKm + " km since last transmission service";

            case "FULL_INSPECTION":
                int inspKm = currentMileage - (vehicle.getLastFullInspectionMileage() != null ? vehicle.getLastFullInspectionMileage() : 0);
                return inspKm + " km since last full inspection";

            case "TIRE_PRESSURE_CHECK":
                return "Monthly tire pressure check due";

            case "FLUID_LEVELS_CHECK":
                return "Monthly fluid levels check due";

            case "GENERAL_INSPECTION":
                return "Quarterly general inspection due";

            case "BRAKE_CHECK":
                return "6-month brake check due";

            case "FULL_SERVICE":
                return "Annual full service due (battery & cooling system)";

            case "BRAKE_FLUID_REPLACEMENT":
                return "Brake fluid replacement due (every 2 years)";

            case "COOLANT_REPLACEMENT":
                return "Coolant replacement due (every 2 years)";

            default:
                return "Maintenance required based on schedule";
        }
    }

    // ===== HELPER METHODS (Keep existing ones) =====

    private void updateVehicleMaintenanceRecords(Maintenance maintenance) {
        Vehicle vehicle = maintenance.getVehicle();
        Integer currentMileage = vehicle.getKilometrage() != null
                ? vehicle.getKilometrage().intValue()
                : null;

        if (currentMileage == null) return;

        switch (maintenance.getMaintenanceType()) {
            case "OIL_CHANGE":
                vehicle.setLastOilChangeMileage(currentMileage);
                vehicle.setLastOilChangeDate(LocalDate.now());
                break;
            case "AIR_FILTER_REPLACEMENT":
                vehicle.setLastAirFilterMileage(currentMileage);
                break;
            case "CABIN_FILTER_REPLACEMENT":
                vehicle.setLastCabinFilterMileage(currentMileage);
                break;
            case "BRAKE_INSPECTION":
            case "BRAKE_CHECK":
                vehicle.setLastBrakeInspectionMileage(currentMileage);
                vehicle.setLastBrakeInspectionDate(LocalDate.now());
                break;
            case "SPARK_PLUG_REPLACEMENT":
                vehicle.setLastSparkPlugMileage(currentMileage);
                break;
            case "FUEL_FILTER_REPLACEMENT":
                vehicle.setLastFuelFilterMileage(currentMileage);
                break;
            case "TIMING_BELT_REPLACEMENT":
                vehicle.setLastTimingBeltMileage(currentMileage);
                break;
            case "TRANSMISSION_SERVICE":
                vehicle.setLastTransmissionOilMileage(currentMileage);
                break;
            case "FULL_INSPECTION":
                vehicle.setLastFullInspectionMileage(currentMileage);
                break;
            case "TIRE_PRESSURE_CHECK":
                vehicle.setLastTirePressureCheckDate(LocalDate.now());
                break;
            case "FLUID_LEVELS_CHECK":
                vehicle.setLastFluidLevelsCheckDate(LocalDate.now());
                break;
            case "GENERAL_INSPECTION":
                vehicle.setLastGeneralInspectionDate(LocalDate.now());
                break;
            case "FULL_SERVICE":
                vehicle.setLastBatteryCheckDate(LocalDate.now());
                vehicle.setLastCoolingCheckDate(LocalDate.now());
                break;
            case "BRAKE_FLUID_REPLACEMENT":
                vehicle.setLastBrakeFluidDate(LocalDate.now());
                break;
            case "COOLANT_REPLACEMENT":
                vehicle.setLastCoolantDate(LocalDate.now());
                break;
        }

        vehicleRepository.save(vehicle);
    }

    private void checkAndUpdateVehicleStatus(Long vehicleId) {
        long incompleteCount = maintenanceRepository.findByVehicleId(vehicleId).stream()
                .filter(m -> m.getStatus() != MaintenanceStatus.COMPLETED)
                .count();

        if (incompleteCount == 0) {
            Vehicle vehicle = vehicleRepository.findById(vehicleId)
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));

            if (vehicle.getStatus() == VehicleStatus.IN_REVISION) {
                vehicle.setStatus(VehicleStatus.AVAILABLE);
                vehicleRepository.save(vehicle);

                notificationService.createNotification(
                        "Vehicle Available",
                        "Vehicle " + vehicle.getPlateNumber() + " has completed all maintenance and is now available for use",
                        NotificationType.GENERAL,
                        vehicle.getId(),
                        "/vehicles"
                );
            }
        }
    }
}