package com.horseracing.repository;

import com.horseracing.entity.PointRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointRuleRepository extends JpaRepository<PointRule, Long> {
    @Query("SELECT pr FROM PointRule pr WHERE pr.season.id = ?1")
    List<PointRule> findBySeasonId(Long seasonId);
    
    @Query("SELECT pr FROM PointRule pr WHERE pr.season.id = ?1 AND pr.position = ?2")
    Optional<PointRule> findBySeasonIdAndPosition(Long seasonId, Integer position);
}
