package com.ContractBilling.commissions.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entity representing a Contractor (person who gets placed at clients)
 */
@Entity
@Table(name = "contractor")
@Data
public class Contractor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContractorType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContractorStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Set default values before persisting to database
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ContractorStatus.ACTIVE;
        }
    }

    /**
     * Update timestamp before updating record
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
