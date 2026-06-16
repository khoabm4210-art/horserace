package com.horseracing.repository;

import com.horseracing.entity.HorseRanking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HorseRankingRepository extends JpaRepository<HorseRanking, Long> {
    @Query("SELECT hr FROM HorseRanking hr WHERE hr.seasonId = ?1 AND hr.horseId = ?2")
    Optional<HorseRanking> findBySeasonAndHorse(Long seasonId, Long horseId);

    @Query("SELECT hr FROM HorseRanking hr WHERE hr.seasonId = ?1 ORDER BY hr.totalPoints DESC")
    Page<HorseRanking> findBySeasonOrderByPoints(Long seasonId, Pageable pageable);
}
