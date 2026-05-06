package com.bank.pfe1.service;


import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bank.pfe1.entity.Manage;
import com.bank.pfe1.repository.ManageRepository;
import com.bank.pfe1.entity.Employee;
import com.bank.pfe1.entity.Maintenance;
import com.bank.pfe1.entity.Mission;
import com.bank.pfe1.entity.MissionStatus;
import com.bank.pfe1.entity.NotificationType;
import com.bank.pfe1.entity.Vehicle;
import com.bank.pfe1.entity.VehicleStatus;
import com.bank.pfe1.repository.EmployeeRepository;
import com.bank.pfe1.repository.MaintenanceRepository;
import com.bank.pfe1.repository.MissionRepository;
import com.bank.pfe1.repository.TechnicalCheckRepository;
import com.bank.pfe1.repository.VehicleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService; // ✅ ADD THIS
    private final EmployeeRepository employeeRepository;  // ✅ Add this line
    private final MissionRepository missionRepository;  // ✅ ADD
    private final MaintenanceRepository maintenanceRepository;
    private final ManageRepository manageRepository;  // ✅ ADD THIS
    // ✅ ADD
    private final TechnicalCheckRepository technicalCheckRepository;
    // ① Define the mileage threshold (you can move this to application.properties)
    private static final int MILEAGE_THRESHOLD = 10000; // km

    // ② Call this after saving the vehicle whenever km changes
    // ✅ CORRECT - using getKilometrage()
    private void checkMileageAlert(Vehicle vehicle) {
        if (vehicle.getKilometrage() != null  // ← Use getKilometrage()
                && vehicle.getKilometrage() >= MILEAGE_THRESHOLD
                && vehicle.getStatus() != VehicleStatus.IN_REVISION) {

            notificationService.createNotification(
                    "⚠️ Maintenance Required",
                    "Vehicle " + vehicle.getPlateNumber()
                            + " has reached " + vehicle.getKilometrage()
                            + " km and needs a maintenance check.",
                    NotificationType.MAINTENANCE,
                    vehicle.getId(),
                    "/maintenance?vehicleId=" + vehicle.getId()
            );
        }
    }
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> getVehiclesByStatus(VehicleStatus status) {
        return vehicleRepository.findByStatus(status);
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found!"));
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findAvailableVehicles();
    }

    @Transactional
    public Vehicle createVehicle(Vehicle vehicle) {
        if (vehicleRepository.existsByPlateNumber(vehicle.getPlateNumber())) {
            throw new RuntimeException("Plate number already exists!");
        }

        // ✅ If car is 10+ years old -> REFORMED, else -> AVAILABLE
        int currentYear = LocalDate.now().getYear();
        if (vehicle.getYear() != null && (currentYear - vehicle.getYear()) >= 10) {
            vehicle.setStatus(VehicleStatus.REFORMED);
        } else {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
        }

        Vehicle saved = vehicleRepository.save(vehicle);
        auditLogService.log("CREATE", "Vehicle", String.valueOf(saved.getId()), "Created vehicle: " + saved.getPlateNumber());
        return saved;
    }



    @Transactional
    public Vehicle updateVehicle(Long id, Vehicle updated) {
        Vehicle vehicle = getVehicleById(id);
        vehicle.setPlateNumber(updated.getPlateNumber());
        vehicle.setModel(updated.getModel());
        vehicle.setYear(updated.getYear());
        vehicle.setKilometrage(updated.getKilometrage());
        vehicle.setFuelType(updated.getFuelType());
        vehicle.setBrand(updated.getBrand());
        vehicle.setVehicleType(updated.getVehicleType());

        // ✅ Only auto-update status if vehicle is currently AVAILABLE or ASSIGNED
        // Don't reset BREAKDOWN, IN_REVISION, IN_MISSION, or REFORMED statuses on edit
        int currentYear = LocalDate.now().getYear();
        if (vehicle.getYear() != null && (currentYear - vehicle.getYear()) >= 10) {
            if (vehicle.getStatus() == VehicleStatus.AVAILABLE || vehicle.getStatus() == VehicleStatus.ASSIGNED) {
                vehicle.setStatus(VehicleStatus.REFORMED);
            }
        } else if (vehicle.getStatus() == VehicleStatus.REFORMED) {
            // If vehicle was REFORMED but is now under 10 years old (year corrected), make it AVAILABLE
            vehicle.setStatus(VehicleStatus.AVAILABLE);
        }

        Vehicle result = vehicleRepository.save(vehicle);
        auditLogService.log("UPDATE", "Vehicle", String.valueOf(id), "Updated vehicle: " + vehicle.getPlateNumber());
        return result;
    }

    @Transactional
    public Vehicle updateStatus(Long id, VehicleStatus status) {
        Vehicle vehicle = getVehicleById(id);
        vehicle.setStatus(status);
        Vehicle updated = vehicleRepository.save(vehicle);
        auditLogService.log("UPDATE_STATUS", "Vehicle", String.valueOf(updated.getId()), "Status changed to " + status + " for vehicle: " + updated.getPlateNumber());
        return updated;
    }

    @Transactional
    public Vehicle putInMaintenance(Long id) {
        Vehicle vehicle = getVehicleById(id);
        if (vehicle.getStatus() == VehicleStatus.IN_MISSION) {
            throw new RuntimeException("Cannot put vehicle in maintenance while on mission!");
        }
        vehicle.setStatus(VehicleStatus.IN_REVISION);
        Vehicle updated = vehicleRepository.save(vehicle);
        auditLogService.log("MAINTENANCE", "Vehicle", String.valueOf(updated.getId()), "Vehicle put in maintenance: " + updated.getPlateNumber());
        // ✅ ADD NOTIFICATION
        notificationService.createNotification(
                "Vehicle in Maintenance",
                "Vehicle " + vehicle.getPlateNumber() + " has been sent to maintenance",
                NotificationType.MAINTENANCE,  // ✅ CORRECT - no "Notification."
                vehicle.getId(),
                "/maintenance"  // ✅ Added link
        );

        return updated;
    }

    @Transactional
    public Vehicle reportBreakdown(Long id) {
        Vehicle vehicle = getVehicleById(id);
        if (vehicle.getStatus() == VehicleStatus.IN_MISSION) {
            throw new RuntimeException("Cannot report breakdown while on mission!");
        }
        vehicle.setStatus(VehicleStatus.BREAKDOWN);
        Vehicle updated = vehicleRepository.save(vehicle);
        auditLogService.log("BREAKDOWN", "Vehicle", String.valueOf(updated.getId()), "Breakdown reported for vehicle: " + updated.getPlateNumber());

        // ✅ ADD NOTIFICATION
        notificationService.createNotification(
                "Vehicle Breakdown",
                "Vehicle " + vehicle.getPlateNumber() + " has reported a breakdown",
                NotificationType.VEHICLE_BREAKDOWN,  // ✅ Use NotificationType directly
                vehicle.getId(),
                "/vehicles"  // ✅ Added link
        );
        return updated;
    }

    @Transactional
    public Vehicle markAvailable(Long id) {
        Vehicle vehicle = getVehicleById(id);
        if (vehicle.getStatus() == VehicleStatus.REFORMED) {
            throw new RuntimeException("Cannot make a reformed vehicle available!");
        }
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        Vehicle updated = vehicleRepository.save(vehicle);
        auditLogService.log("AVAILABLE", "Vehicle", String.valueOf(updated.getId()), "Vehicle marked available: " + updated.getPlateNumber());

        // ✅ ADD NOTIFICATION
        notificationService.createNotification(
                "Vehicle Available",
                "Vehicle " + vehicle.getPlateNumber() + " is now available for use",
                NotificationType.GENERAL,  // ✅ Use NotificationType directly
                vehicle.getId(),
                "/vehicles"  // ✅ Added link
        );
        return updated;
    }

    @Transactional
    public Vehicle reformVehicle(Long id) {
        Vehicle vehicle = getVehicleById(id);
        if (vehicle.getStatus() == VehicleStatus.IN_MISSION) {
            throw new RuntimeException("Cannot reform vehicle while on mission!");
        }
        vehicle.setStatus(VehicleStatus.REFORMED);
        Vehicle updated = vehicleRepository.save(vehicle);
        auditLogService.log("REFORM", "Vehicle", String.valueOf(updated.getId()), "Vehicle reformed: " + updated.getPlateNumber());
        // ✅ ADD NOTIFICATION (optional - for reformed vehicles)
        notificationService.createNotification(
                "Vehicle Reformed",
                "Vehicle " + vehicle.getPlateNumber() + " has been marked as reformed",
                NotificationType.GENERAL,  // ✅ Use NotificationType directly
                vehicle.getId(),
                "/vehicles"  // ✅ Added link
        );
        return updated;
    }


    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle vehicle = getVehicleById(id);

        VehicleStatus status = vehicle.getStatus();

        if (status == VehicleStatus.IN_MISSION) {
            throw new RuntimeException("Cannot delete vehicle! It is currently on a mission.");
        }

        if (status == VehicleStatus.ASSIGNED) {
            throw new RuntimeException("Cannot delete vehicle! It is currently assigned to a driver.");
        }

        List<Mission> allMissions = missionRepository.findByVehicleId(id);
        boolean hasActiveMissions = allMissions.stream()
                .anyMatch(m -> m.getStatus() == MissionStatus.PLANNED || m.getStatus() == MissionStatus.IN_PROGRESS);
        if (hasActiveMissions) {
            throw new RuntimeException("Cannot delete vehicle! It has active/pending missions.");
        }

        List<Maintenance> allMaintenances = maintenanceRepository.findByVehicleId(id);
        boolean hasActiveMaintenance = allMaintenances.stream()
                .anyMatch(m -> m.getStatus() != com.bank.pfe1.entity.MaintenanceStatus.COMPLETED);
        if (hasActiveMaintenance) {
            throw new RuntimeException("Cannot delete vehicle! It has active maintenance in progress.");
        }

        // ✅ FIX: Set vehicle to null in all manage history records
        List<Manage> manageRecords = manageRepository.findByVehicleIdOrderByAssignedAtDesc(id);
        manageRecords.forEach(m -> m.setVehicle(null));
        manageRepository.saveAll(manageRecords);

        // ✅ Set vehicle to null in completed missions (history)
        allMissions.forEach(m -> m.setVehicle(null));
        missionRepository.saveAll(allMissions);

        // ✅ Set vehicle to null in completed maintenance (history)
        allMaintenances.forEach(m -> m.setVehicle(null));
        maintenanceRepository.saveAll(allMaintenances);

        if (vehicle.getAssignedTo() != null) {
            Employee employee = vehicle.getAssignedTo();
            employee.setCurrentlyAssignedVehicle(null);
            employee.setVehicleAssignedAt(null);
            employeeRepository.save(employee);
            vehicle.setAssignedTo(null);
            vehicle.setAssignedAt(null);
        }

        auditLogService.log("DELETE", "Vehicle", String.valueOf(id), "Deleted vehicle ID: " + id);
        vehicleRepository.deleteById(id);
    }}
