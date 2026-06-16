package com.horseracing.storage;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface StorageService {
    String upload(MultipartFile file, String subPath) throws IOException;
    void delete(String fileUrl) throws IOException;
    String getUrl(String storagePath);
}
