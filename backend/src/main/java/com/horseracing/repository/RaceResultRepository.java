package com.horseracing.repository;

import com.horseracing.entity.RaceResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RaceResultRepository extends JpaRepository<RaceResult, Long> {
    @Query("SELECT r FROM RaceResult r WHERE r.deleted = 0 AND r.raceId = ?1")
    Optional<RaceResult> findByRace(Long raceId);
}
