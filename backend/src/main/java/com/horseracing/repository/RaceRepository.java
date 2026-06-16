package com.horseracing.repository;

import com.horseracing.entity.Race;
import com.horseracing.enums.RaceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RaceRepository extends JpaRepository<Race, Long> {
    @Query("SELECT r FROM Race r WHERE r.deleted = 0")
    Page<Race> findAllActive(Pageable pageable);
    
    @Query("SELECT r FROM Race r WHERE r.deleted = 0 AND r.status = ?1")
    Page<Race> findByStatusActive(RaceStatus status, Pageable pageable);
    
    @Query("SELECT r FROM Race r WHERE r.deleted = 0 AND r.season.id = ?1")
    Page<Race> findBySeasonIdActive(Long seasonId, Pageable pageable);
    
    @Query("SELECT r FROM Race r WHERE r.deleted = 0 AND r.name LIKE %?1%")
    Page<Race> searchActive(String keyword, Pageable pageable);
}
