package com.bank.pfe1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "gas_coupon")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GasCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String batchNumber;
    private Integer quantityBought;
    private Integer quantityRemaining;
    private Double fuelAmount;
    private LocalDate buyDate;
    private LocalDate expiryDate;
    private Double totalCost;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    private String notes;
}