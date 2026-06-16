package com.horseracing.repository;

import com.horseracing.entity.Season;
import com.horseracing.enums.SeasonStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {
    @Query("SELECT s FROM Season s WHERE s.deleted = 0")
    Page<Season> findAllActive(Pageable pageable);
    
    @Query("SELECT s FROM Season s WHERE s.deleted = 0 AND s.status = ?1")
    Page<Season> findByStatusActive(SeasonStatus status, Pageable pageable);
}
