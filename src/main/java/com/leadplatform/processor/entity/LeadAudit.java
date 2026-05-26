package com.leadplatform.processor.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "lead_audit")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lead_id", length = 100)
    private String leadId;

    @Column(name = "event_name", length = 100)
    private String eventName;

    @Column(name = "remarks", columnDefinition = "text")
    private String remarks;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
