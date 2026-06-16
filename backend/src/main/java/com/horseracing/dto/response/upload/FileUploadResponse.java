package com.horseracing.dto.response.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private Long fileId;
    private String url;
    private String fileName;
    private String fileType;
    private String fileCategory;
    private Long fileSize;
}
