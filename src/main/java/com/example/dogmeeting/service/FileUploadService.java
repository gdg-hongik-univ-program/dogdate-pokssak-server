package com.example.dogmeeting.service;

import org.springframework.web.multipart.MultipartFile;
import java.time.Duration;

public interface FileUploadService {
    /**
     * @return The S3 object key of the uploaded file.
     */
    String uploadDogImage(MultipartFile file, Long userId, Long dogId);

    /**
     * @return The S3 object key of the uploaded file.
     */
    String uploadProfileImage(MultipartFile file, Long userId);

    void deleteFile(String fileKey);

    boolean isValidImageFile(MultipartFile file);

    /**
     * Generates a presigned URL for viewing an S3 object.
     * @param key The S3 object key.
     * @param duration The duration for which the URL is valid.
     * @return A presigned URL.
     */
    String generatePresignedGetUrl(String key, Duration duration);
} 