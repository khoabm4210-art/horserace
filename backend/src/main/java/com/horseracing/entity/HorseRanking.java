package com.horseracing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "horse_rankings", uniqueConstraints = {
    @UniqueConstraint(name = "uq_horse_season", columnNames = {"horse_id", "season_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorseRanking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_id", nullable = false)
    private Horse horse;

    @Column(nullable = false)
    private Integer totalPoints = 0;

    @Column(nullable = false)
    private Integer totalRaces = 0;

    @Column(nullable = false)
    private Integer totalWins = 0;

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
