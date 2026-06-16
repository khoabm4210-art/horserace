package com.horseracing.repository;

import com.horseracing.entity.Jockey;
import com.horseracing.enums.JockeyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JockeyRepository extends JpaRepository<Jockey, Long> {
    Optional<Jockey> findByLicenseNumber(String licenseNumber);

    @Query("SELECT j FROM Jockey j WHERE j.deleted = 0 AND j.ownerId = ?1")
    Page<Jockey> findByOwner(Long ownerId, Pageable pageable);

    @Query("SELECT j FROM Jockey j WHERE j.deleted = 0 AND j.status = ?1")
    Page<Jockey> findByStatus(JockeyStatus status, Pageable pageable);

    @Query("SELECT j FROM Jockey j WHERE j.deleted = 0 AND (j.name LIKE %?1% OR j.licenseNumber LIKE %?1%)")
    Page<Jockey> searchByKeyword(String keyword, Pageable pageable);

    @Query("SELECT j FROM Jockey j WHERE j.deleted = 0")
    Page<Jockey> findAllActive(Pageable pageable);
}
