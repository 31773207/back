package com.bank.pfe1.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;      // who did it
    private String action;        // CREATE, UPDATE, DELETE
    private String entityType;    // Vehicle, Mission, Driver...
    private String entityId;      // ID of affected record
    private String description;   // human readable detail

    @CreationTimestamp
    private LocalDateTime timestamp;  // when
}
