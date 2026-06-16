package com.horseracing.service.impl;

import com.horseracing.dto.response.upload.FileUploadResponse;
import com.horseracing.entity.FileUpload;
import com.horseracing.entity.User;
import com.horseracing.exception.BadRequestException;
import com.horseracing.exception.FileUploadException;
import com.horseracing.exception.ResourceNotFoundException;
import com.horseracing.repository.FileUploadRepository;
import com.horseracing.repository.UserRepository;
import com.horseracing.service.FileUploadService;
import com.horseracing.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@Transactional
public class FileUploadServiceImpl implements FileUploadService {
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "pdf");

    @Autowired
    private FileUploadRepository fileUploadRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StorageService storageService;

    @Override
    public FileUploadResponse upload(MultipartFile file, String targetType, String fileCategory, Long targetId, Long uploadedBy) throws Exception {
        // Validate file
        if (file.isEmpty()) {
            throw new BadRequestException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File size exceeds 5MB limit");
        }

        String extension = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("File type not allowed. Allowed: jpg, jpeg, png, gif, pdf");
        }

        User uploadedByUser = userRepository.findById(uploadedBy)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        try {
            String url = storageService.upload(file, targetType);
            String fileName = file.getOriginalFilename();
            String mimeType = file.getContentType();

            FileUpload fileUpload = FileUpload.builder()
                .fileName(fileName)
                .originalName(fileName)
                .fileType(mimeType)
                .fileCategory(fileCategory)
                .fileSize(file.getSize())
                .filePath(url)
                .url(url)
                .uploadedBy(uploadedByUser)
                .targetType(targetType)
                .targetId(targetId)
                .build();

            FileUpload saved = fileUploadRepository.save(fileUpload);
            log.info("File uploaded: {} for {}", fileName, targetType);

            return FileUploadResponse.builder()
                .fileId(saved.getId())
                .url(saved.getUrl())
                .fileName(saved.getFileName())
                .fileType(saved.getFileType())
                .fileCategory(saved.getFileCategory())
                .fileSize(saved.getFileSize())
                .build();
        } catch (Exception e) {
            log.error("File upload failed: {}", e.getMessage());
            throw new FileUploadException("File upload failed: " + e.getMessage());
        }
    }

    @Override
    public void delete(Long fileId) throws Exception {
        FileUpload fileUpload = fileUploadRepository.findById(fileId)
            .orElseThrow(() -> new ResourceNotFoundException("File not found"));

        try {
            storageService.delete(fileUpload.getUrl());
            fileUploadRepository.delete(fileUpload);
            log.info("File deleted: {}", fileId);
        } catch (Exception e) {
            log.error("File deletion failed: {}", e.getMessage());
            throw new FileUploadException("File deletion failed: " + e.getMessage());
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
