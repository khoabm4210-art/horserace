package com.horseracing.entity;

import com.horseracing.enums.Gender;
import com.horseracing.enums.JockeyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "jockeys", indexes = {
    @Index(name = "idx_jockey_owner", columnList = "owner_id"),
    @Index(name = "idx_jockey_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Jockey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column
    private Integer experienceYears;

    @Column(precision = 5, scale = 2)
    private Double weight;

    @Column(length = 50)
    private String licenseNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(length = 500)
    private String avatarUrl;

    @Column(length = 500)
    private String licenseScanUrl;

    @Column(length = 500)
    private String medicalCertUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JockeyStatus status = JockeyStatus.PENDING;

    @Column(nullable = false)
    private Integer deleted = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
