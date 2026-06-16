package com.horseracing.repository;

import com.horseracing.entity.Jockey;
import com.horseracing.enums.JockeyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JockeyRepository extends JpaRepository<Jockey, Long> {
    @Query("SELECT j FROM Jockey j WHERE j.deleted = 0")
    Page<Jockey> findAllActive(Pageable pageable);
    
    @Query("SELECT j FROM Jockey j WHERE j.deleted = 0 AND j.status = ?1")
    Page<Jockey> findByStatusActive(JockeyStatus status, Pageable pageable);
    
    @Query("SELECT j FROM Jockey j WHERE j.deleted = 0 AND j.owner.id = ?1")
    Page<Jockey> findByOwnerIdActive(Long ownerId, Pageable pageable);
    
    @Query("SELECT j FROM Jockey j WHERE j.deleted = 0 AND j.fullName LIKE %?1%")
    Page<Jockey> searchActive(String keyword, Pageable pageable);
}
