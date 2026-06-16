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
    @Query("SELECT jr FROM JockeyRanking jr WHERE jr.seasonId = ?1 AND jr.jockeyId = ?2")
    Optional<JockeyRanking> findBySeasonAndJockey(Long seasonId, Long jockeyId);

    @Query("SELECT jr FROM JockeyRanking jr WHERE jr.seasonId = ?1 ORDER BY jr.totalPoints DESC")
    Page<JockeyRanking> findBySeasonOrderByPoints(Long seasonId, Pageable pageable);
}
