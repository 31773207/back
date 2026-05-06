package com.bank.pfe1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "mission")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String destination;
    private String departLocation;
    private String purpose;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private MissionType missionType;

    @Enumerated(EnumType.STRING)
    private MissionStatus status;

    // ✅ C'est ICI qu'on assigne chauffeur + véhicule ensemble!
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = true,
            foreignKey = @ForeignKey(
                    name = "fk_mission_vehicle",
                    foreignKeyDefinition = "FOREIGN KEY (vehicle_id) REFERENCES vehicle(id) ON DELETE SET NULL"
            ))
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    // ✅ Pour l'historique
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;

    private LocalDateTime startedAt;  // Add this field

    private Double finalKilometrage;  // Kilometrage at mission completion


}
