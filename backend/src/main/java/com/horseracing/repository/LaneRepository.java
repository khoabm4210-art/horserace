package com.horseracing.repository;

import com.horseracing.entity.Lane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaneRepository extends JpaRepository<Lane, Long> {
    @Query("SELECT l FROM Lane l WHERE l.deleted = 0 AND l.raceId = ?1")
    List<Lane> findByRace(Long raceId);

    @Query("SELECT l FROM Lane l WHERE l.deleted = 0 AND l.registrationId = ?1")
    Optional<Lane> findByRegistration(Long registrationId);

    @Query("SELECT MAX(l.laneNumber) FROM Lane l WHERE l.deleted = 0 AND l.raceId = ?1")
    Integer findMaxLaneNumber(Long raceId);
}
