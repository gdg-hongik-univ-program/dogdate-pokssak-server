package com.example.dogmeeting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3FileUploadService implements FileUploadService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    // 허용된 이미지 파일 확장자
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"
    );

    // 최대 파일 크기 (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Override
    public String uploadDogImage(MultipartFile file, Long userId, Long dogId) {
        try {
            log.info("S3 업로드 시작: userId={}, dogId={}, fileName={}", userId, dogId, file.getOriginalFilename());
            
            if (!isValidImageFile(file)) {
                throw new IllegalArgumentException("유효하지 않은 이미지 파일입니다.");
            }

            String fileName = generateFileName("dogs", userId, dogId, file.getOriginalFilename());
            log.info("생성된 파일명: {}", fileName);
            
            return uploadToS3(file, fileName);
        } catch (Exception e) {
            log.error("S3 업로드 실패: userId={}, dogId={}, error={}", userId, dogId, e.getMessage(), e);
            throw new RuntimeException("S3 업로드에 실패했습니다: " + e.getMessage(), e);
        }
    }

    @Override
    public String uploadProfileImage(MultipartFile file, Long userId) {
        if (!isValidImageFile(file)) {
            throw new IllegalArgumentException("유효하지 않은 이미지 파일입니다.");
        }

        String fileName = generateFileName("profiles", userId, null, file.getOriginalFilename());
        return uploadToS3(file, fileName);
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            // URL에서 S3 키 추출
            String key = extractKeyFromUrl(fileUrl);
            
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteRequest);
            log.info("파일 삭제 완료: {}", key);
        } catch (Exception e) {
            log.error("파일 삭제 실패: {}", fileUrl, e);
            throw new RuntimeException("파일 삭제에 실패했습니다.", e);
        }
    }

    @Override
    public boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        // 파일 크기 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            return false;
        }

        // 파일 확장자 검증
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return false;
        }

        String extension = getFileExtension(fileName).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    private String uploadToS3(MultipartFile file, String fileName) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 업로드된 파일의 URL 생성
            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", 
                    bucketName, region, fileName);
            
            log.info("파일 업로드 완료: {}", fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", fileName, e);
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    private String generateFileName(String folder, Long userId, Long dogId, String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        
        if (dogId != null) {
            return String.format("%s/user_%d/dog_%d/%s_%s.%s", 
                    folder, userId, dogId, timestamp, uuid, extension);
        } else {
            return String.format("%s/user_%d/%s_%s.%s", 
                    folder, userId, timestamp, uuid, extension);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private String extractKeyFromUrl(String url) {
        // URL에서 버킷명 이후의 키 부분만 추출
        // 예: https://bucket.s3.region.amazonaws.com/folder/file.jpg -> folder/file.jpg
        try {
            URL urlObj = new URL(url);
            return urlObj.getPath().substring(1); // 맨 앞의 '/' 제거
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 S3 URL입니다: " + url);
        }
    }
} 