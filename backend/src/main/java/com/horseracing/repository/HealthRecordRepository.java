package com.horseracing.repository;

import com.horseracing.entity.HealthRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    Page<HealthRecord> findByHorseId(Long horseId, Pageable pageable);
    Page<HealthRecord> findByRaceId(Long raceId, Pageable pageable);
}
