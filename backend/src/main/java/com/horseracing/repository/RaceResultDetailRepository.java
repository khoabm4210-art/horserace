package com.horseracing.repository;

import com.horseracing.entity.RaceResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaceResultDetailRepository extends JpaRepository<RaceResultDetail, Long> {
    @Query("SELECT d FROM RaceResultDetail d WHERE d.result.id = ?1 ORDER BY d.finishPosition ASC")
    List<RaceResultDetail> findByResultId(Long resultId);
    
    @Query("SELECT d FROM RaceResultDetail d WHERE d.result.id = ?1 AND d.finishPosition = ?2")
    RaceResultDetail findByResultIdAndFinishPosition(Long resultId, Integer position);
    
    @Modifying
    @Query("DELETE FROM RaceResultDetail d WHERE d.result.id = ?1")
    void deleteByResultId(Long resultId);
}
