package com.bank.pfe1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;  // ✅ Add this
import java.time.LocalDate;  // ✅ ADD THIS IMPORT

@Entity
@Table(name = "manage")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Manage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = true,
            foreignKey = @ForeignKey(
                    name = "fk_manage_vehicle",
                    foreignKeyDefinition = "FOREIGN KEY (vehicle_id) REFERENCES vehicle(id) ON DELETE SET NULL"
            ))
    private Vehicle vehicle;

    private LocalDateTime assignedAt;   // When the assignment was created
    private LocalDateTime removedAt;    // When the assignment ended

    private LocalDate startDate;        // ✅ ADD THIS - Assignment start date (can be different from assignedAt)
    private LocalDate endDate;          // ✅ ADD THIS - Assignment end date (for planned end)
    private String organization;
    private String notes;
}