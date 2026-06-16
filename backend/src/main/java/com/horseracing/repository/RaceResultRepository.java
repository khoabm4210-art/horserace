package com.horseracing.repository;

import com.horseracing.entity.RaceResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RaceResultRepository extends JpaRepository<RaceResult, Long> {
    Optional<RaceResult> findByRaceId(Long raceId);
}
