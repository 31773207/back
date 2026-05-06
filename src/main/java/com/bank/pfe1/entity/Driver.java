package com.bank.pfe1.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "driver")
@Data
@NoArgsConstructor
@AllArgsConstructor
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Driver extends Employee {

    private String licenseNumber;
    private String licenseExpiry;

    // ✅ You can add driver-specific fields here
    // Vehicle assignment will be handled in Mission
}