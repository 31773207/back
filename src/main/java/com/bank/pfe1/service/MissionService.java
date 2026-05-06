package com.bank.pfe1.service;

import com.bank.pfe1.entity.*;
import com.bank.pfe1.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final MissionTimeRepository missionTimeRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final OrganizationRepository organizationRepository;
    private final ManageService manageService;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;  // ✅ ADD THIS for notifications

    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }

    public Mission getMissionById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + id));
    }

    public List<Mission> getMissionsByStatus(MissionStatus status) {
        return missionRepository.findByStatus(status);
    }

    public List<Mission> getMissionsByDriver(Long driverId) {
        return missionRepository.findByDriverId(driverId);
    }

    @Transactional
    public Mission createMission(Mission mission) {
        // Validate dates are not in the past
        LocalDate today = LocalDate.now();
        if (mission.getStartDate() != null && mission.getStartDate().isBefore(today)) {
            throw new RuntimeException("Start date cannot be in the past!");
        }
        if (mission.getEndDate() != null && mission.getEndDate().isBefore(today)) {
            throw new RuntimeException("End date cannot be in the past!");
        }
        if (mission.getStartDate() != null && mission.getEndDate() != null && mission.getEndDate().isBefore(mission.getStartDate())) {
            throw new RuntimeException("End date must be after start date!");
        }

        Driver driver = driverRepository.findById(mission.getDriver().getId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        Vehicle vehicle = vehicleRepository.findById(mission.getVehicle().getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        // Validate and fetch organization if provided
        if (mission.getOrganization() != null && mission.getOrganization().getId() != null) {
            Organization organization = organizationRepository.findById(mission.getOrganization().getId())
                    .orElseThrow(() -> new RuntimeException("Organization not found"));
            mission.setOrganization(organization);
        }

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new RuntimeException("Vehicle is not available for mission! Current status: " + vehicle.getStatus());
        }

        List<Mission> activeMissions = missionRepository.findByDriverIdAndStatus(driver.getId(), MissionStatus.IN_PROGRESS);
        if (!activeMissions.isEmpty()) {
            throw new RuntimeException("Driver is already on a mission!");
        }

        mission.setDriver(driver);
        mission.setVehicle(vehicle);
        mission.setStatus(MissionStatus.PLANNED);
        mission.setAssignedAt(LocalDateTime.now());

        // Update vehicle - track which driver has it
        vehicle.setStatus(VehicleStatus.IN_MISSION);
        vehicle.setAssignedTo(driver);
        vehicle.setAssignedAt(LocalDateTime.now());
        vehicleRepository.save(vehicle);

        // Update driver - track which vehicle they have
        driver.setCurrentlyAssignedVehicle(vehicle);
        driver.setVehicleAssignedAt(LocalDateTime.now());
        driver.setVehicleRemovedAt(null);
        driver.setEmployeeStatus(EmployeeStatus.ON_MISSION);
        driverRepository.save(driver);

        // Create history
        Mission saved = missionRepository.save(mission);
        manageService.createAssignmentHistory(driver, vehicle, mission.getStartDate(), mission.getEndDate(), "MISSION");
        auditLogService.log("CREATE", "Mission", String.valueOf(saved.getId()), "Created mission to: " + saved.getDestination());
        return saved;
    }

    @Transactional
    public Mission updateMission(Long id, Mission updated) {
        Mission mission = getMissionById(id);
        mission.setDestination(updated.getDestination());
        mission.setDepartLocation(updated.getDepartLocation());
        mission.setPurpose(updated.getPurpose());
        mission.setStartDate(updated.getStartDate());
        mission.setEndDate(updated.getEndDate());
        mission.setMissionType(updated.getMissionType());
        return missionRepository.save(mission);
    }

    @Transactional
    public Mission startMission(Long id) {
        Mission mission = getMissionById(id);
        mission.setStatus(MissionStatus.IN_PROGRESS);
        mission.setStartedAt(LocalDateTime.now());

        LocalDate today = LocalDate.now();
        if (mission.getStartDate() != null && today.isBefore(mission.getStartDate())) {
            mission.setStartDate(today);
        }

        Driver driver = mission.getDriver();
        driver.setEmployeeStatus(EmployeeStatus.ON_MISSION);
        driverRepository.save(driver);
        return missionRepository.save(mission);
    }

    @Transactional
    public Mission completeMission(Long id, Double finalKilometrage) {
        Mission mission = getMissionById(id);
        mission.setStatus(MissionStatus.COMPLETED);
        mission.setCompletedAt(LocalDateTime.now());
        mission.setFinalKilometrage(finalKilometrage);

        Vehicle vehicle = mission.getVehicle();
        Driver driver = mission.getDriver();

        // Update vehicle kilometrage if provided
        if (finalKilometrage != null) {
            vehicle.setKilometrage(finalKilometrage);
        }

        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicle.setAssignedTo(null);
        vehicle.setAssignedAt(null);
        vehicleRepository.save(vehicle);

        driver.setCurrentlyAssignedVehicle(null);
        driver.setVehicleAssignedAt(null);
        driver.setVehicleRemovedAt(LocalDateTime.now());
        driver.setEmployeeStatus(EmployeeStatus.AVAILABLE);
        driverRepository.save(driver);

        manageService.completeAssignmentHistory(driver, vehicle);

        // ✅ ADD NOTIFICATION
        notificationService.createNotification(
                "Mission Completed",
                "Mission to " + mission.getDestination() + " completed. Final KM: " + finalKilometrage,
                NotificationType.MISSION_COMPLETED,
                mission.getId(),
                "/missions"
        );

        Mission saved = missionRepository.save(mission);
        auditLogService.log("UPDATE", "Mission", String.valueOf(id), "Completed mission ID: " + id);
        return saved;
    }

    @Transactional
    public Mission cancelMission(Long id) {
        Mission mission = getMissionById(id);
        mission.setStatus(MissionStatus.CANCELLED);

        Vehicle vehicle = mission.getVehicle();
        if (vehicle.getAssignedTo() != null) {
            vehicle.setStatus(VehicleStatus.ASSIGNED);
        } else {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
        }
        vehicleRepository.save(vehicle);
        Mission saved = missionRepository.save(mission);
        auditLogService.log("UPDATE", "Mission", String.valueOf(id), "Cancelled mission ID: " + id);
        return saved;
    }

    @Transactional
    public void deleteMission(Long id) {
        Mission mission = getMissionById(id);
        if (mission.getStatus() == MissionStatus.IN_PROGRESS) {
            throw new RuntimeException("Cannot delete a mission that is in progress!");
        }
        missionRepository.deleteById(id);
    }

    public List<Driver> getAvailableDrivers() {
        return driverRepository.findAvailableDrivers();
    }

    // ✅ NEW METHOD - Get available drivers ordered by least missions
    public List<Driver> getAvailableDriversOrderedByLeastMissions() {
        return missionRepository.findAvailableDriversOrderedByLeastMissions();
    }

    // Mission Time methods
    public List<MissionTime> getTimesByMission(Long missionId) {
        return missionTimeRepository.findByMissionId(missionId);
    }

    public MissionTime addMissionTime(MissionTime missionTime) {
        return missionTimeRepository.save(missionTime);
    }

    public void deleteMissionTime(Long id) {
        missionTimeRepository.deleteById(id);
    }
}