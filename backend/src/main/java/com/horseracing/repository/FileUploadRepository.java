package com.horseracing.repository;

import com.horseracing.entity.FileUpload;
import com.horseracing.enums.FileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {
    @Query("SELECT f FROM FileUpload f WHERE f.deleted = 0 AND f.targetType = ?1 AND f.targetId = ?2")
    List<FileUpload> findByTarget(String targetType, Long targetId);

    @Query("SELECT f FROM FileUpload f WHERE f.deleted = 0 AND f.targetType = ?1 AND f.targetId = ?2 AND f.fileType = ?3")
    List<FileUpload> findByTargetAndType(String targetType, Long targetId, FileType fileType);
}
