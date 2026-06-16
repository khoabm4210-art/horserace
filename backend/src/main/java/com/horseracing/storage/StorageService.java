package com.horseracing.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    /**
     * Upload file và trả về URL truy cập công khai.
     * @param file      MultipartFile từ request
     * @param subPath   thư mục con, VD: "HORSE", "JOCKEY"
     * @return URL đầy đủ để truy cập file
     */
    String upload(MultipartFile file, String subPath) throws IOException;

    /**
     * Xóa file theo URL hoặc storage key.
     */
    void delete(String fileUrl) throws IOException;

    /**
     * Lấy URL truy cập công khai từ storage key/path.
     */
    String getUrl(String storagePath);
}
