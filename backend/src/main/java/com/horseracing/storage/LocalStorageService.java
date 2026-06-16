package com.horseracing.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(name = "file.storage-provider", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {
    
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public String upload(MultipartFile file, String subPath) throws IOException {
        String ext = getExtension(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "." + ext;

        Path dir = Paths.get(uploadDir, subPath);
        Files.createDirectories(dir);
        Files.copy(file.getInputStream(), dir.resolve(fileName));

        log.info("File uploaded: {} to {}/{}", fileName, subPath, fileName);
        return baseUrl + "/uploads/" + subPath + "/" + fileName;
    }

    @Override
    public void delete(String fileUrl) throws IOException {
        try {
            String relativePath = fileUrl.replaceFirst(baseUrl + "/uploads/", "");
            Path filePath = Paths.get(uploadDir, relativePath);
            Files.deleteIfExists(filePath);
            log.info("File deleted: {}", relativePath);
        } catch (Exception e) {
            log.error("Error deleting file: {}", fileUrl, e);
            throw e;
        }
    }

    @Override
    public String getUrl(String storagePath) {
        return baseUrl + "/uploads/" + storagePath;
    }

    private String getExtension(String filename) {
        if (filename == null) {
            return "bin";
        }
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot + 1).toLowerCase() : "bin";
    }
}
