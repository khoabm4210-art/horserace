package com.horseracing.repository;

import com.horseracing.entity.RaceRegistration;
import com.horseracing.enums.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RaceRegistrationRepository extends JpaRepository<RaceRegistration, Long> {
    @Query("SELECT COUNT(r) FROM RaceRegistration r WHERE r.raceId = ?1 AND r.status = 'APPROVED'")
    int countApprovedByRace(Long raceId);

    @Query("SELECT r FROM RaceRegistration r WHERE r.deleted = 0 AND r.raceId = ?1 AND r.status = ?2")
    Page<RaceRegistration> findByRaceAndStatus(Long raceId, RegistrationStatus status, Pageable pageable);

    @Query("SELECT r FROM RaceRegistration r WHERE r.deleted = 0 AND r.ownerId = ?1")
    Page<RaceRegistration> findByOwner(Long ownerId, Pageable pageable);

    @Query("SELECT r FROM RaceRegistration r WHERE r.deleted = 0 AND r.raceId = ?1 AND r.horseId = ?2")
    Optional<RaceRegistration> findByRaceAndHorse(Long raceId, Long horseId);

    @Query("SELECT r FROM RaceRegistration r WHERE r.deleted = 0 AND r.raceId = ?1")
    Page<RaceRegistration> findByRace(Long raceId, Pageable pageable);
}
