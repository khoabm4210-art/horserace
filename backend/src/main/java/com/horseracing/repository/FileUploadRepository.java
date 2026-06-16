package com.horseracing.repository;

import com.horseracing.entity.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {
    @Query("SELECT f FROM FileUpload f WHERE f.targetType = ?1 AND f.targetId = ?2")
    List<FileUpload> findByTargetTypeAndTargetId(String targetType, Long targetId);
    
    @Query("SELECT f FROM FileUpload f WHERE f.uploadedBy.id = ?1")
    List<FileUpload> findByUploadedById(Long uploadedBy);
}
