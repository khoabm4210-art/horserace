package com.horseracing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "health_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_id", nullable = false)
    private Horse horse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id")
    private Race race;

    @Column(precision = 6, scale = 2)
    private Double weight;

    @Column(length = 100)
    private String healthStatus;

    @Column(length = 500)
    private String certificateUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by")
    private User recordedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime recordedAt = LocalDateTime.now();
}
