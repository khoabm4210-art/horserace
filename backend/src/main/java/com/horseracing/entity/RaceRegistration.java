package com.horseracing.entity;

import com.horseracing.enums.RegistrationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "race_registrations", indexes = {
    @Index(name = "idx_registration_status", columnList = "status"),
    @Index(name = "idx_registration_owner", columnList = "owner_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uq_horse_race", columnNames = {"horse_id", "race_id"}),
    @UniqueConstraint(name = "uq_jockey_race", columnNames = {"jockey_id", "race_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaceRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_id", nullable = false)
    private Horse horse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jockey_id", nullable = false)
    private Jockey jockey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RegistrationStatus status = RegistrationStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String rejectReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();

    @Column
    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private User reviewedBy;
}
