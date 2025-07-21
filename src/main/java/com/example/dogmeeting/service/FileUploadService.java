package com.example.dogmeeting.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    String uploadDogImage(MultipartFile file, Long userId, Long dogId);
    String uploadProfileImage(MultipartFile file, Long userId);
    void deleteFile(String fileUrl);
    boolean isValidImageFile(MultipartFile file);
} 