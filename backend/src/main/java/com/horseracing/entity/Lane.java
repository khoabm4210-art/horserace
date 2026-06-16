package com.horseracing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "lanes", uniqueConstraints = {
    @UniqueConstraint(name = "uq_race_lane", columnNames = {"race_id", "lane_number"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lane {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id", nullable = false)
    private Race race;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id", nullable = false, unique = true)
    private RaceRegistration registration;

    @Column(nullable = false)
    private Integer laneNumber;

    @Column(nullable = false, updatable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private User assignedBy;
}
