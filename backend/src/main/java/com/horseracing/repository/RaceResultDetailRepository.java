package com.horseracing.repository;

import com.horseracing.entity.RaceResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaceResultDetailRepository extends JpaRepository<RaceResultDetail, Long> {
    @Query("SELECT rd FROM RaceResultDetail rd WHERE rd.deleted = 0 AND rd.resultId = ?1 ORDER BY rd.finishPosition ASC")
    List<RaceResultDetail> findByResult(Long resultId);
}
