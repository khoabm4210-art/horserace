package com.horseracing.repository;

import com.horseracing.entity.RaceResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RaceResultRepository extends JpaRepository<RaceResult, Long> {
    @Query("SELECT r FROM RaceResult r WHERE r.race.id = ?1")
    Optional<RaceResult> findByRaceId(Long raceId);
    
    @Query("SELECT r FROM RaceResult r WHERE r.isPublished = ?1")
    Page<RaceResult> findByIsPublished(Integer isPublished, Pageable pageable);
    
    @Query("SELECT COUNT(r) FROM RaceResult r WHERE r.isPublished = 1")
    long countPublished();
}
