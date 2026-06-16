package com.horseracing.repository;

import com.horseracing.entity.JockeyRanking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JockeyRankingRepository extends JpaRepository<JockeyRanking, Long> {
    @Query("SELECT jr FROM JockeyRanking jr WHERE jr.seasonId = ?1")
    Page<JockeyRanking> findBySeasonId(Long seasonId, Pageable pageable);
    
    @Query("SELECT jr FROM JockeyRanking jr WHERE jr.seasonId = ?1 AND jr.jockey.id = ?2")
    Optional<JockeyRanking> findBySeasonIdAndJockeyId(Long seasonId, Long jockeyId);
}
