package com.horseracing.repository;

import com.horseracing.entity.RaceResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RaceResultDetailRepository extends JpaRepository<RaceResultDetail, Long> {
    List<RaceResultDetail> findByResultIdOrderByFinishPosition(Long resultId);
}
