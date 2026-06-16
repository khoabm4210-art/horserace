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
    @Query("SELECT r FROM RaceRegistration r WHERE r.status = ?1")
    Page<RaceRegistration> findByStatus(RegistrationStatus status, Pageable pageable);
    
    @Query("SELECT r FROM RaceRegistration r WHERE r.race.id = ?1 AND r.status = ?2")
    Page<RaceRegistration> findByRaceAndStatus(Long raceId, RegistrationStatus status, Pageable pageable);
    
    @Query("SELECT r FROM RaceRegistration r WHERE r.owner.id = ?1")
    Page<RaceRegistration> findByOwner(Long ownerId, Pageable pageable);
    
    @Query("SELECT COUNT(r) FROM RaceRegistration r WHERE r.race.id = ?1 AND r.status = ?2")
    long countByRaceAndStatus(Long raceId, RegistrationStatus status);
    
    Optional<RaceRegistration> findByRaceIdAndHorseId(Long raceId, Long horseId);
    Optional<RaceRegistration> findByRaceIdAndJockeyId(Long raceId, Long jockeyId);
}
