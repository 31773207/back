package com.bank.pfe1.scheduler;

import com.bank.pfe1.entity.*;
import com.bank.pfe1.repository.*;
import com.bank.pfe1.service.ManageService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MissionScheduler {

    private final MissionRepository missionRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final ManageService manageService;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void autoUpdateMissions() {
        LocalDate today = LocalDate.now();

        List<Mission> toStart = missionRepository.findByStatusAndStartDateLessThanEqual(
                MissionStatus.PLANNED, today);

        for (Mission mission : toStart) {
            mission.setStatus(MissionStatus.IN_PROGRESS);
            mission.setStartedAt(LocalDateTime.now());

            Vehicle vehicle = mission.getVehicle();
            vehicle.setStatus(VehicleStatus.IN_MISSION);
            vehicle.setAssignedTo(mission.getDriver());
            vehicle.setAssignedAt(LocalDateTime.now());
            vehicleRepository.save(vehicle);

            Driver driver = mission.getDriver();
            driver.setEmployeeStatus(EmployeeStatus.ON_MISSION);
            driver.setCurrentlyAssignedVehicle(vehicle);
            driver.setVehicleAssignedAt(LocalDateTime.now());
            driverRepository.save(driver);

            missionRepository.save(mission);
            System.out.println("✅ Auto-started mission ID: " + mission.getId());
        }
    }
}