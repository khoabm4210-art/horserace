package com.horseracing.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "race_result_details", uniqueConstraints = {
    @UniqueConstraint(name = "uq_result_position", columnNames = {"result_id", "finish_position"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaceResultDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id", nullable = false)
    private RaceResult result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id", nullable = false)
    private RaceRegistration registration;

    @Column(name = "horse_id", nullable = false)
    private Long horseId;

    @Column(name = "jockey_id", nullable = false)
    private Long jockeyId;

    @Column(nullable = false)
    private Integer finishPosition;

    @Column(length = 20)
    private String finishTime;

    @Column(nullable = false)
    private Integer pointsEarned = 0;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
