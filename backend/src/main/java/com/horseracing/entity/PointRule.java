package com.horseracing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "point_rules", indexes = {
    @Index(name = "idx_season_position", columnList = "season_id, position")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uq_season_position", columnNames = {"season_id", "position"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false)
    private Integer point;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
