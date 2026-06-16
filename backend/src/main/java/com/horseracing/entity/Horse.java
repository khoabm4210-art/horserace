package com.horseracing.entity;

import com.horseracing.enums.Gender;
import com.horseracing.enums.HorseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "horses", indexes = {
    @Index(name = "idx_horse_code", columnList = "code"),
    @Index(name = "idx_horse_owner", columnList = "owner_id"),
    @Index(name = "idx_horse_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Horse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String breed;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(length = 50)
    private String color;

    @Column(precision = 6, scale = 2)
    private Double weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(length = 500)
    private String avatarUrl;

    @Column(length = 500)
    private String passportUrl;

    @Column(length = 500)
    private String healthCertUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HorseStatus status = HorseStatus.PENDING;

    @Column(nullable = false)
    private Integer deleted = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
