package com.horseracing.repository;

import com.horseracing.entity.FileUpload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {
    Page<FileUpload> findByTargetTypeAndTargetId(String targetType, Long targetId, Pageable pageable);
}
