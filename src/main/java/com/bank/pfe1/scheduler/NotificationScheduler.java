package com.bank.pfe1.scheduler;

import com.bank.pfe1.entity.*;
import com.bank.pfe1.repository.*;
import com.bank.pfe1.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final MissionRepository missionRepository;
    private final MaintenanceRepository maintenanceRepository;
    private final TechnicalCheckRepository technicalCheckRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    // ✅ REMOVE readOnly = true - we need to INSERT notifications
    @Scheduled(fixedRate = 3600000)  // Every hour
    @Transactional  // ← No readOnly = true
    public void checkUpcomingEvents() {
        checkMissionEndings();
        checkMaintenanceDue();
        checkTechnicalChecksExpiring();
    }

    private void checkMissionEndings() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<Mission> endingMissions = missionRepository.findAll().stream()
                .filter(m -> m.getStatus() == MissionStatus.IN_PROGRESS)
                .filter(m -> m.getEndDate() != null)
                .filter(m -> m.getEndDate().isEqual(today) || m.getEndDate().isEqual(tomorrow))
                .toList();

        for (Mission mission : endingMissions) {
            String title = "Mission Ending Soon";
            String message = String.format("Mission #%d to %s ends on %s. Vehicle: %s, Driver: %s %s",
                    mission.getId(), mission.getDestination(), mission.getEndDate(),
                    mission.getVehicle() != null ? mission.getVehicle().getPlateNumber() : "N/A",
                    mission.getDriver() != null ? mission.getDriver().getFirstName() : "",
                    mission.getDriver() != null ? mission.getDriver().getLastName() : "");

            createNotificationIfNotExists(title, message, NotificationType.MISSION_ENDING, mission.getId());
        }
    }

    private void checkMaintenanceDue() {
        LocalDate today = LocalDate.now();

        List<Maintenance> maintenances = maintenanceRepository.findAll().stream()
                .filter(m -> m.getStatus() == MaintenanceStatus.IN_PROGRESS || m.getStatus() == MaintenanceStatus.SCHEDULED)
                .filter(m -> m.getEndDate() != null)
                .filter(m -> m.getEndDate().isEqual(today) || m.getEndDate().isBefore(today))
                .toList();

        for (Maintenance maintenance : maintenances) {
            String title = "Maintenance Due";
            String message = String.format("Maintenance for vehicle %s (Type: %s) is due or overdue. End date: %s",
                    maintenance.getVehicle() != null ? maintenance.getVehicle().getPlateNumber() : "N/A",
                    maintenance.getMaintenanceType(),
                    maintenance.getEndDate());

            createNotificationIfNotExists(title, message, NotificationType.MAINTENANCE_DUE, maintenance.getId());
        }
    }

    private void checkTechnicalChecksExpiring() {
        LocalDate today = LocalDate.now();
        LocalDate warningDate = today.plusDays(7);

        List<TechnicalCheck> checks = technicalCheckRepository.findAll().stream()
                .filter(tc -> tc.getStatus() == TechnicalCheckStatus.VALID)
                .filter(tc -> tc.getExpiryDate() != null)
                .filter(tc -> tc.getExpiryDate().isEqual(today) || tc.getExpiryDate().isEqual(warningDate) || tc.getExpiryDate().isBefore(today))
                .toList();

        for (TechnicalCheck check : checks) {
            String title = check.getExpiryDate().isBefore(today)
                    ? "Technical Check Expired"
                    : "Technical Check Expiring Soon";
            String message = String.format("Technical check for vehicle %s %s on %s",
                    check.getVehicle() != null ? check.getVehicle().getPlateNumber() : "N/A",
                    check.getExpiryDate().isBefore(today) ? "expired" : "expires",
                    check.getExpiryDate());

            createNotificationIfNotExists(title, message, NotificationType.TECHNICAL_CHECK_EXPIRING, check.getId());
        }
    }

    private void createNotificationIfNotExists(String title, String message, NotificationType type, Long relatedId) {
        // Check if a similar unread notification already exists
        boolean exists = notificationRepository.findByReadFalseOrderByCreatedAtDesc().stream()
                .anyMatch(n -> n.getType() == type && n.getRelatedId() != null && n.getRelatedId().equals(relatedId));

        if (!exists) {
            notificationService.createNotification(title, message, type, relatedId, null);
        }
    }
}

