package com.horseracing.repository;

import com.horseracing.entity.RequestHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestHistoryRepository extends JpaRepository<RequestHistory, Long> {
    Page<RequestHistory> findByTargetTypeAndTargetId(String targetType, Long targetId, Pageable pageable);
}
