package com.bank.pfe1.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "coupon_assignment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private GasCoupon batch;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private Integer quantity;
    private Integer remaining;
    private Integer usedQuantity;
    private LocalDate assignedDate;
    private LocalDate usedDate;
    private String status;
}