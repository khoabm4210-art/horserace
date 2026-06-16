package com.horseracing.service;

import com.horseracing.dto.response.upload.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    FileUploadResponse upload(MultipartFile file, String targetType, String fileCategory, Long targetId, Long uploadedBy) throws Exception;
    void delete(Long fileId) throws Exception;
}
