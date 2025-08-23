package com.example.dogmeeting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
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
    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * @return The S3 object key of the uploaded file.
     */
    @Override
    public String uploadDogImage(MultipartFile file, Long userId, Long dogId) {
        log.info("S3 강아지 이미지 업로드 시작: userId={}, dogId={}, fileName={}", userId, dogId, file.getOriginalFilename());
        validateImageFile(file);
        String fileName = generateFileName("dogs", userId, dogId, file.getOriginalFilename());
        return uploadToS3(file, fileName);
    }

    /**
     * @return The S3 object key of the uploaded file.
     */
    @Override
    public String uploadProfileImage(MultipartFile file, Long userId) {
        log.info("S3 프로필 이미지 업로드 시작: userId={}, fileName={}", userId, file.getOriginalFilename());
        validateImageFile(file);
        String fileName = generateFileName("profiles", userId, null, file.getOriginalFilename());
        return uploadToS3(file, fileName);
    }

    @Override
    public void deleteFile(String fileKey) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .build();
            s3Client.deleteObject(deleteRequest);
            log.info("S3 파일 삭제 완료: {}", fileKey);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}", fileKey, e);
            throw new RuntimeException("S3 파일 삭제에 실패했습니다.", e);
        }
    }

    @Override
    public boolean isValidImageFile(MultipartFile file) {
        try {
            validateImageFile(file);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String generatePresignedGetUrl(String key, Duration duration) {
        if (key == null || key.isBlank()) {
            return null;
        }
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(duration)
                    .getObjectRequest(getObjectRequest)
                    .build();

            return s3Presigner.presignGetObject(presignRequest).url().toExternalForm();
        } catch (Exception e) {
            log.error("S3 Presigned URL 생성 실패: key={}", key, e);
            // In a production environment, you might want to return a default/placeholder image URL here
            return null;
        }
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 비어있습니다.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("파일 크기가 너무 큽니다. (최대 5MB)");
        }
        String extension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("허용되지 않는 파일 확장자입니다. (허용: jpg, jpeg, png, gif, webp)");
        }
    }

    /**
     * @return The S3 object key of the uploaded file.
     */
    private String uploadToS3(MultipartFile file, String fileName) {
        log.info("Attempting to upload to S3: bucket={}, key={}", bucketName, fileName);
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            log.info("파일 업로드 완료: {}", fileName);

            // Return the object key instead of the full URL
            return fileName;
        } catch (IOException e) {
            log.error("S3 파일 업로드 실패: {}", fileName, e);
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    private String generateFileName(String folder, Long userId, Long dogId, String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        if (dogId != null) {
            return String.format("%s/user_%d/dog_%d/%s_%s.%s", folder, userId, dogId, timestamp, uuid, extension);
        } else {
            return String.format("%s/user_%d/%s_%s.%s", folder, userId, timestamp, uuid, extension);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
