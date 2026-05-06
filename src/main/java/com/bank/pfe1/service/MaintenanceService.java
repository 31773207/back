package com.bank.pfe1.service;

import com.bank.pfe1.entity.Maintenance;
import com.bank.pfe1.entity.MaintenanceStatus;
import com.bank.pfe1.entity.Vehicle;
import com.bank.pfe1.entity.VehicleStatus;
import com.bank.pfe1.entity.NotificationType;
import com.bank.pfe1.repository.MaintenanceRepository;
import com.bank.pfe1.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository repository;
    private final VehicleRepository vehicleRepository;
    private final NotificationService notificationService;

    @PostConstruct
    public void init() {
        System.out.println("✅✅✅ MaintenanceService LOADED! ✅✅✅");
    }

    // ===== CRUD =====

    public List<Maintenance> getAll() {
        return repository.findAll();
    }

    public Optional<Maintenance> getById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Maintenance create(Maintenance maintenance) {
        Long vehicleId = maintenance.getVehicle().getId();
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found with id: " + vehicleId));

        maintenance.setVehicle(vehicle);

        if (vehicle.getStatus() == VehicleStatus.AVAILABLE ||
                vehicle.getStatus() == VehicleStatus.REFORMED) {
            vehicle.setStatus(VehicleStatus.IN_REVISION);
            vehicleRepository.save(vehicle);
        }

        Maintenance saved = repository.save(maintenance);

        notificationService.createNotification(
                "Maintenance Scheduled",
                "Maintenance scheduled for vehicle " + saved.getVehicle().getPlateNumber() +
                        " - Type: " + saved.getMaintenanceType() + " on " + saved.getStartDate(),
                NotificationType.MAINTENANCE,
                saved.getVehicle().getId(),
                "/maintenance"
        );

        return saved;
    }

    @Transactional
    public Maintenance update(Long id, Maintenance updated) {
        Maintenance existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance not found"));

        MaintenanceStatus oldStatus = existing.getStatus();
        MaintenanceStatus newStatus = updated.getStatus();

        if (updated.getVehicle() != null && updated.getVehicle().getId() != null) {
            Vehicle vehicle = vehicleRepository.findById(updated.getVehicle().getId())
                    .orElseThrow(() -> new RuntimeException("Vehicle not found"));
            existing.setVehicle(vehicle);
        }

        existing.setStartDate(updated.getStartDate());
        existing.setEndDate(updated.getEndDate());
        existing.setMaintenanceType(updated.getMaintenanceType());
        existing.setCost(updated.getCost());
        existing.setDescription(updated.getDescription());
        existing.setStatus(updated.getStatus());

        // ✅ REMOVED the VehicleParts block here

        // If status is changing to COMPLETED, set end date to today
        if (newStatus == MaintenanceStatus.COMPLETED && oldStatus != MaintenanceStatus.COMPLETED) {
            existing.setEndDate(LocalDate.now());
        } else {
            existing.setEndDate(updated.getEndDate());
        }

        Maintenance saved = repository.save(existing);

        // Send notifications after saving
        if (newStatus == MaintenanceStatus.COMPLETED && oldStatus != MaintenanceStatus.COMPLETED) {
            notificationService.createNotification(
                    "Maintenance Completed",
                    "Maintenance completed for vehicle " + saved.getVehicle().getPlateNumber(),
                    NotificationType.MAINTENANCE,
                    saved.getVehicle().getId(),
                    "/vehicles"
            );
            checkAndUpdateVehicleStatus(saved.getVehicle().getId());
            updateVehicleMaintenanceRecords(saved);
        }

        if (newStatus == MaintenanceStatus.IN_PROGRESS && oldStatus != MaintenanceStatus.IN_PROGRESS) {
            notificationService.createNotification(
                    "Maintenance In Progress",
                    "Maintenance work started for vehicle " + saved.getVehicle().getPlateNumber(),
                    NotificationType.MAINTENANCE,
                    saved.getVehicle().getId(),
                    "/maintenance"
            );
        }

        return saved;
    }
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // ===== CUSTOM METHODS =====

    public List<Maintenance> getByVehicle(Long vehicleId) {
        return repository.findByVehicleId(vehicleId);
    }

    public List<Maintenance> getByStatus(MaintenanceStatus status) {
        return repository.findByStatus(status);
    }

    public List<Maintenance> getByType(String type) {
        return repository.findByMaintenanceType(type);
    }

    public List<Maintenance> getByDateRange(LocalDate start, LocalDate end) {
        return repository.findByStartDateBetween(start, end);
    }

    public long countByStatus(MaintenanceStatus status) {
        return repository.countByStatus(status);
    }

    private void checkAndUpdateVehicleStatus(Long vehicleId) {
        long incompleteCount = repository.findByVehicleId(vehicleId).stream()
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

    // ===== AUTO MAINTENANCE METHODS (Called by Scheduler) =====

    @Transactional
    public void createAutoMaintenance(Vehicle vehicle, String maintenanceType, String reason) {
        boolean alreadyScheduled = repository.findByVehicleId(vehicle.getId()).stream()
                .filter(m -> m.getMaintenanceType().equals(maintenanceType))
                .anyMatch(m -> m.getStatus() != MaintenanceStatus.COMPLETED);

        if (alreadyScheduled) {
            System.out.println("⏭️ Auto maintenance skipped: " + maintenanceType + " already scheduled for " + vehicle.getPlateNumber());
            return;
        }

        Maintenance maintenance = Maintenance.builder()
                .vehicle(vehicle)
                .startDate(LocalDate.now())
                .maintenanceType(maintenanceType)
                .cost(BigDecimal.ZERO)
                .description("AUTO-SCHEDULED: " + reason)
                .status(MaintenanceStatus.SCHEDULED)
                .build();

        Maintenance saved = repository.save(maintenance);

        if (vehicle.getStatus() == VehicleStatus.AVAILABLE || vehicle.getStatus() == VehicleStatus.REFORMED) {
            vehicle.setStatus(VehicleStatus.IN_REVISION);
            vehicleRepository.save(vehicle);
        }

        notificationService.createNotification(
                "Auto Maintenance Scheduled",
                "Vehicle " + vehicle.getPlateNumber() + " needs " + maintenanceType + ". " + reason,
                NotificationType.MAINTENANCE,
                vehicle.getId(),
                "/maintenance"
        );

        System.out.println("✅ Auto-created: " + maintenanceType + " for vehicle " + vehicle.getPlateNumber());
    }

    @Transactional
    public void updateVehicleMaintenanceRecords(Maintenance maintenance) {
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
            default:
                if (maintenance.getMaintenanceType().contains("OIL")) {
                    vehicle.setLastOilChangeMileage(currentMileage);
                    vehicle.setLastOilChangeDate(LocalDate.now());
                }
                break;
        }

        vehicleRepository.save(vehicle);
        System.out.println("📝 Updated vehicle records for " + maintenance.getMaintenanceType() + " on " + vehicle.getPlateNumber());
    }
}