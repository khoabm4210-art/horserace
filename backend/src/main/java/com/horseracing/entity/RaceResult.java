package com.horseracing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "race_results", uniqueConstraints = {
    @UniqueConstraint(name = "uq_race_id", columnNames = "race_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaceResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id", nullable = false, unique = true)
    private Race race;

    @Column(nullable = false)
    private Integer isPublished = 0;

    @Column
    private LocalDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "published_by")
    private User publishedBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
