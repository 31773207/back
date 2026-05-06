package com.bank.pfe1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Entity
@Table(name = "employee")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  // ✅ ADD THIS

public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String address;
    private String dateOfBirth;

    @Column(nullable = false)
    private String employeeType; // "DRIVER" or "EMPLOYEE"

    //Employee status
    @Enumerated(EnumType.STRING)
    private EmployeeStatus employeeStatus = EmployeeStatus.AVAILABLE;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    // Pour tracker le véhicule assigné (hors mission)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "currently_assigned_vehicle_id")
    private Vehicle currentlyAssignedVehicle;

    // Date d'assignation du véhicule
    private LocalDateTime vehicleAssignedAt;

    //  Date de retrait du véhicule
    private LocalDateTime vehicleRemovedAt;

    //  Pour l'affichage dans le frontend
    @Transient
    public String getDisplayId() {
        String prefix = "EMPLOYEE".equals(this.employeeType) ? "Employee" : "Driver";
        return prefix + " N0" + this.id;
    }

    @Transient
    public String getDisplayType() {
        return "EMPLOYEE".equals(this.employeeType) ? "Employee" : "Driver";
    }

}