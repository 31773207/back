/*package com.bank.pfe1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.time.LocalDate;

@Entity
@Table(name = "vehicle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String plateNumber;

    private String model;
    private Integer year;
    private Double kilometrage;
    private String fuelType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    private String color;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private VehicleType vehicleType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_to_id")
    @JsonIgnore
    private Employee assignedTo;

    private LocalDateTime assignedAt;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Mission> missions;

    // ===== MAINTENANCE TRACKING FIELDS =====
    // Mileage-based tracking
    @Column(name = "last_oil_change_mileage")
    private Integer lastOilChangeMileage;

    @Column(name = "last_oil_change_date")
    private LocalDate lastOilChangeDate;

    @Column(name = "last_air_filter_mileage")
    private Integer lastAirFilterMileage;

    @Column(name = "last_cabin_filter_mileage")
    private Integer lastCabinFilterMileage;

    @Column(name = "last_brake_inspection_mileage")
    private Integer lastBrakeInspectionMileage;

    @Column(name = "last_brake_inspection_date")
    private LocalDate lastBrakeInspectionDate;

    @Column(name = "last_spark_plug_mileage")
    private Integer lastSparkPlugMileage;

    @Column(name = "last_fuel_filter_mileage")
    private Integer lastFuelFilterMileage;

    @Column(name = "last_timing_belt_mileage")
    private Integer lastTimingBeltMileage;

    @Column(name = "last_transmission_oil_mileage")
    private Integer lastTransmissionOilMileage;

    @Column(name = "last_full_inspection_mileage")
    private Integer lastFullInspectionMileage;

    @Column(name = "last_tire_rotation_mileage")
    private Integer lastTireRotationMileage;

    @Column(name = "last_major_service_mileage")
    private Integer lastMajorServiceMileage;

    @Column(name = "last_major_service_date")
    private LocalDate lastMajorServiceDate;

    // Time-based tracking
    @Column(name = "last_tire_pressure_check_date")
    private LocalDate lastTirePressureCheckDate;

    @Column(name = "last_fluid_levels_check_date")
    private LocalDate lastFluidLevelsCheckDate;

    @Column(name = "last_general_inspection_date")
    private LocalDate lastGeneralInspectionDate;

    @Column(name = "last_battery_check_date")
    private LocalDate lastBatteryCheckDate;

    @Column(name = "last_cooling_check_date")
    private LocalDate lastCoolingCheckDate;

    @Column(name = "last_brake_fluid_date")
    private LocalDate lastBrakeFluidDate;

    @Column(name = "last_coolant_date")
    private LocalDate lastCoolantDate;

    // Vehicle configuration
    @Column(name = "engine_type")
    private String engineType; // PETROL, DIESEL, ELECTRIC, HYBRID

    @Column(name = "oil_type")
    private String oilType; // CONVENTIONAL, SYNTHETIC, SYNTHETIC_BLEND

}*/

package com.bank.pfe1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.time.LocalDate;

@Entity
@Table(name = "vehicle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String plateNumber;

    private String model;
    private Integer year;
    private Double kilometrage;
    private String fuelType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    private String color;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private VehicleType vehicleType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_to_id")
    @JsonIgnore
    private Employee assignedTo;

    private LocalDateTime assignedAt;

    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Mission> missions;

    // ===== MAINTENANCE TRACKING FIELDS =====
    // Mileage-based tracking
    @Column(name = "last_oil_change_mileage")
    private Integer lastOilChangeMileage;

    @Column(name = "last_oil_change_date")
    private LocalDate lastOilChangeDate;

    @Column(name = "last_air_filter_mileage")
    private Integer lastAirFilterMileage;

    @Column(name = "last_cabin_filter_mileage")
    private Integer lastCabinFilterMileage;

    @Column(name = "last_brake_inspection_mileage")
    private Integer lastBrakeInspectionMileage;

    @Column(name = "last_brake_inspection_date")
    private LocalDate lastBrakeInspectionDate;

    @Column(name = "last_spark_plug_mileage")
    private Integer lastSparkPlugMileage;

    @Column(name = "last_fuel_filter_mileage")
    private Integer lastFuelFilterMileage;

    @Column(name = "last_timing_belt_mileage")
    private Integer lastTimingBeltMileage;

    @Column(name = "last_transmission_oil_mileage")
    private Integer lastTransmissionOilMileage;

    @Column(name = "last_full_inspection_mileage")
    private Integer lastFullInspectionMileage;

    @Column(name = "last_tire_rotation_mileage")
    private Integer lastTireRotationMileage;

    @Column(name = "last_major_service_mileage")
    private Integer lastMajorServiceMileage;

    @Column(name = "last_major_service_date")
    private LocalDate lastMajorServiceDate;

    // Time-based tracking
    @Column(name = "last_tire_pressure_check_date")
    private LocalDate lastTirePressureCheckDate;

    @Column(name = "last_fluid_levels_check_date")
    private LocalDate lastFluidLevelsCheckDate;

    @Column(name = "last_general_inspection_date")
    private LocalDate lastGeneralInspectionDate;

    @Column(name = "last_battery_check_date")
    private LocalDate lastBatteryCheckDate;

    @Column(name = "last_cooling_check_date")
    private LocalDate lastCoolingCheckDate;

    @Column(name = "last_brake_fluid_date")
    private LocalDate lastBrakeFluidDate;

    @Column(name = "last_coolant_date")
    private LocalDate lastCoolantDate;

    // Added technical check expiry date
    @Column(name = "technical_check_expiry")
    private LocalDate technicalCheckExpiry;

    // Inspection center for technical check (CT center)
    @Column(name = "inspection_center")
    private String inspectionCenter;

    // Vehicle configuration
    @Column(name = "engine_type")
    private String engineType; // PETROL, DIESEL, ELECTRIC, HYBRID

    @Column(name = "oil_type")
    private String oilType; // CONVENTIONAL, SYNTHETIC, SYNTHETIC_BLEND


}