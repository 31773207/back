package com.bank.pfe1.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"vehicle"})   // Removed vehicleParts
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @NotBlank(message = "Maintenance type is required")
    @Column(name = "maintenance_type", nullable = false, length = 100)
    private String maintenanceType;

    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cost must be positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MaintenanceStatus status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = true,
            foreignKey = @ForeignKey(
                    name = "fk_maintenance_vehicle",
                    foreignKeyDefinition = "FOREIGN KEY (vehicle_id) REFERENCES vehicle(id) ON DELETE SET NULL"
            ))
    private Vehicle vehicle;

    // ✅ REMOVED vehicleParts List completely

    @AssertTrue(message = "End date must be after start date")
    @Transient
    public boolean isEndDateValid() {
        if (endDate == null) return true;
        if (status == MaintenanceStatus.COMPLETED) {
            return !endDate.isBefore(startDate);
        }
        return endDate.isAfter(startDate);
    }
}