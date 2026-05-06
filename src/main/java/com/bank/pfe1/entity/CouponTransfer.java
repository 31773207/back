package com.bank.pfe1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "coupon_transfer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private GasCoupon batch;

    private Integer quantity;
    private LocalDate transferDate;

    @ManyToOne
    @JoinColumn(name = "to_organization_id")
    private Organization toOrganization;

    private String notes;
}
