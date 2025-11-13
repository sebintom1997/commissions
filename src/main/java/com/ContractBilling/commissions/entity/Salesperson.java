package com.ContractBilling.commissions.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity // This tells Spring this is the database table
@Table(name = "salesperson") // This tells Spring the table name
@Data // This generates getters and setters automatically

public class Salesperson {
   @Id // This is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This is auto-incremented
    private Long id;

   @Column(nullable = false, length = 100)
    private String name;

   @Column(nullable = false, unique = true)
    private String email;

   @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SalespersonStatus status;

   @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

   @Column(name = "updated_at")
    private LocalDateTime updatedAt;

   @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        status = SalespersonStatus.ACTIVE; // Default status
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
