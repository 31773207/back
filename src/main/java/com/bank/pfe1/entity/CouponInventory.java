package com.bank.pfe1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "coupon_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer totalCoupons;
    private Integer availableCoupons;
    private Integer assignedCoupons;
    private Integer usedCoupons;
    private Integer expiredCoupons;
    private LocalDate lastUpdated;
    private String notes;
}