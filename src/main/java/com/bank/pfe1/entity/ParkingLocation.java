package com.bank.pfe1.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parking_location")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String address;

    @ManyToOne
    @JoinColumn(name = "wilaya_id")
    private Wilaya wilaya;

    private boolean active = true;
}