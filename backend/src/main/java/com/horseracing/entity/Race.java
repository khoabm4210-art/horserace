package com.horseracing.entity;

import com.horseracing.enums.RaceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "races", indexes = {
    @Index(name = "idx_race_status", columnList = "status"),
    @Index(name = "idx_race_season", columnList = "season_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Race {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private LocalDateTime raceDate;

    @Column(nullable = false, length = 255)
    private String location;

    @Column(nullable = false)
    private Integer distance;

    @Column(nullable = false, length = 50)
    private String surfaceType;

    @Column(nullable = false)
    private Integer maxHorses = 20;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RaceStatus status = RaceStatus.OPEN;

    @Column(nullable = false)
    private Integer isPublished = 0;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(nullable = false)
    private Integer deleted = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
