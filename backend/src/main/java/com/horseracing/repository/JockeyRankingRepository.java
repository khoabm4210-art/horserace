package com.horseracing.repository;

import com.horseracing.entity.JockeyRanking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JockeyRankingRepository extends JpaRepository<JockeyRanking, Long> {
    Page<JockeyRanking> findBySeasonIdOrderByTotalPointsDesc(Long seasonId, Pageable pageable);
    Optional<JockeyRanking> findBySeasonIdAndJockeyId(Long seasonId, Long jockeyId);
}
