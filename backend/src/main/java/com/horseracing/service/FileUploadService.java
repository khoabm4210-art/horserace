package com.horseracing.service;

import com.horseracing.dto.response.upload.FileUploadResponse;
import com.horseracing.enums.FileType;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    FileUploadResponse uploadFile(MultipartFile file, String targetType, FileType fileType, 
                                 Long targetId, Long uploadedBy);
    
    FileUploadResponse getFileById(Long id);
    
    void deleteFile(Long id);
}
