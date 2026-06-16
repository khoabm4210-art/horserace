package com.horseracing.repository;

import com.horseracing.entity.Horse;
import com.horseracing.enums.HorseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HorseRepository extends JpaRepository<Horse, Long> {
    Optional<Horse> findByCode(String code);

    @Query("SELECT h FROM Horse h WHERE h.deleted = 0 AND h.ownerId = ?1")
    Page<Horse> findByOwner(Long ownerId, Pageable pageable);

    @Query("SELECT h FROM Horse h WHERE h.deleted = 0 AND h.status = ?1")
    Page<Horse> findByStatus(HorseStatus status, Pageable pageable);

    @Query("SELECT h FROM Horse h WHERE h.deleted = 0 AND (h.name LIKE %?1% OR h.code LIKE %?1%)")
    Page<Horse> searchByKeyword(String keyword, Pageable pageable);

    @Query("SELECT h FROM Horse h WHERE h.deleted = 0")
    Page<Horse> findAllActive(Pageable pageable);
}
