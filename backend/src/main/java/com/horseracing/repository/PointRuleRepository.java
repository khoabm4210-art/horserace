package com.horseracing.repository;

import com.horseracing.entity.PointRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointRuleRepository extends JpaRepository<PointRule, Long> {
    List<PointRule> findBySeasonId(Long seasonId);
    Optional<PointRule> findBySeasonIdAndPosition(Long seasonId, Integer position);
}
