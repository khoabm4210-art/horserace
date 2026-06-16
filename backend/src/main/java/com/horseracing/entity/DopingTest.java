package com.horseracing.entity;

import com.horseracing.enums.DopingResult;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "doping_tests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DopingTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horse_id", nullable = false)
    private Horse horse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "race_id")
    private Race race;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private DopingResult result;

    @Column(nullable = false, updatable = false)
    private LocalDateTime testedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tested_by")
    private User testedBy;

    @Column(length = 500)
    private String reportUrl;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
