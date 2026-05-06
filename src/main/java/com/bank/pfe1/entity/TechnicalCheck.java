package com.bank.pfe1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "technical_check")
@Data
@NoArgsConstructor
@AllArgsConstructor
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TechnicalCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate checkDate;
    private LocalDate expiryDate;
    private String center;
    private String notes;

    @Enumerated(EnumType.STRING)
    private TechnicalCheckStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = true,
            foreignKey = @ForeignKey(
                    name = "fk_technical_check_vehicle",
                    foreignKeyDefinition = "FOREIGN KEY (vehicle_id) REFERENCES vehicle(id) ON DELETE SET NULL"
            ))
    private Vehicle vehicle;




}