package com.horseracing.repository;

import com.horseracing.entity.Lane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaneRepository extends JpaRepository<Lane, Long> {
    @Query("SELECT l FROM Lane l WHERE l.race.id = ?1 AND l.laneNumber = ?2")
    Optional<Lane> findByRaceIdAndLaneNumber(Long raceId, Integer laneNumber);
    
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Lane l WHERE l.race.id = ?1 AND l.laneNumber = ?2")
    boolean existsByRaceIdAndLaneNumber(Long raceId, Integer laneNumber);
    
    @Query("SELECT l FROM Lane l WHERE l.race.id = ?1 ORDER BY l.laneNumber ASC")
    List<Lane> findByRaceIdOrderByLaneNumber(Long raceId);
    
    @Query("SELECT l FROM Lane l WHERE l.registration.id = ?1")
    Optional<Lane> findByRegistrationId(Long registrationId);
}
