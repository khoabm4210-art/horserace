package com.horseracing.repository;

import com.horseracing.entity.DopingTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DopingTestRepository extends JpaRepository<DopingTest, Long> {
    Page<DopingTest> findByHorseId(Long horseId, Pageable pageable);
    Page<DopingTest> findByRaceId(Long raceId, Pageable pageable);
}
