package com.horseracing.controller;

import com.horseracing.dto.response.upload.FileUploadResponse;
import com.horseracing.dto.response.ApiResponse;
import com.horseracing.service.FileUploadService;
import com.horseracing.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1/upload")
@Tag(name = "File Upload", description = "API tải lên file")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")
public class FileUploadController {
    @Autowired
    private FileUploadService fileUploadService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping
    @Operation(summary = "Tải lên file")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(
            @RequestParam MultipartFile file,
            @RequestParam String targetType,
            @RequestParam String fileCategory,
            @RequestParam(required = false) Long targetId,
            @RequestHeader("Authorization") String token) throws Exception {
        Long uploadedBy = jwtTokenProvider.getUserIdFromToken(token.replace("Bearer ", ""));
        FileUploadResponse response = fileUploadService.upload(file, targetType, fileCategory, targetId, uploadedBy);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok("Tải lên file thành công", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa file")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable Long id) throws Exception {
        fileUploadService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Xóa file thành công", null));
    }
}
