package com.bank.pfe1.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wilaya")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wilaya {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // 01, 02, 03...

    @Column(nullable = false)
    private String name; // Adrar, Chlef, Laghouat...

    private boolean active = true;
}