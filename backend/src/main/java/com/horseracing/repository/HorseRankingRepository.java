package com.horseracing.repository;

import com.horseracing.entity.HorseRanking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HorseRankingRepository extends JpaRepository<HorseRanking, Long> {
    Page<HorseRanking> findBySeasonIdOrderByTotalPointsDesc(Long seasonId, Pageable pageable);
    Optional<HorseRanking> findBySeasonIdAndHorseId(Long seasonId, Long horseId);
}
