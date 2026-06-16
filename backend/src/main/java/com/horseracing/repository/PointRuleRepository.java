package com.horseracing.repository;

import com.horseracing.entity.PointRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointRuleRepository extends JpaRepository<PointRule, Long> {
    @Query("SELECT p FROM PointRule p WHERE p.deleted = 0 AND p.seasonId = ?1 ORDER BY p.position ASC")
    List<PointRule> findBySeason(Long seasonId);

    @Query("SELECT p FROM PointRule p WHERE p.deleted = 0 AND p.seasonId = ?1 AND p.position = ?2")
    Optional<PointRule> findBySeasonAndPosition(Long seasonId, Integer position);
}
