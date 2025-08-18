package com.example.dogmeeting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

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

    // Spring이 application.properties를 기반으로 자동 설정한 S3Client를 주입.
    private final S3Client s3Client;

    // application-secret.properties 파일에서 버킷 이름을 가져옴.
    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    // 허용된 이미지 파일 확장자
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp");

    // 최대 파일 크기 (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 강아지 이미지를 S3에 업로드합니다.
     * @param file 업로드할 이미지 파일
     * @param userId 사용자 ID
     * @param dogId 강아지 ID
     * @return 업로드된 파일의 URL
     */
    @Override
    public String uploadDogImage(MultipartFile file, Long userId, Long dogId) {
        log.info("S3 강아지 이미지 업로드 시작: userId={}, dogId={}, fileName={}", userId, dogId, file.getOriginalFilename());
        validateImageFile(file);
        String fileName = generateFileName("dogs", userId, dogId, file.getOriginalFilename());
        return uploadToS3(file, fileName);
    }

    /**
     * 사용자 프로필 이미지를 S3에 업로드합니다.
     * @param file 업로드할 이미지 파일
     * @param userId 사용자 ID
     * @return 업로드된 파일의 URL
     */
    @Override
    public String uploadProfileImage(MultipartFile file, Long userId) {
        log.info("S3 프로필 이미지 업로드 시작: userId={}, fileName={}", userId, file.getOriginalFilename());
        validateImageFile(file);
        String fileName = generateFileName("profiles", userId, null, file.getOriginalFilename());
        return uploadToS3(file, fileName);
    }

    /**
     * S3에서 파일을 삭제.
     * @param fileUrl 삭제할 파일의 전체 URL
     */
    @Override
    public void deleteFile(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteRequest);
            log.info("S3 파일 삭제 완료: {}", key);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}", fileUrl, e);
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


    /**
     * 업로드된 파일이 유효한 이미지 파일인지 검증합니다.
     * @param file 검증할 파일
     */
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
     * 파일을 S3에 실제로 업로드하는 private 메서드입니다.
     * @param file 업로드할 파일
     * @param fileName S3에 저장될 파일 이름 (경로 포함)
     * @return 업로드된 파일의 전체 URL
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

            // S3Client 유틸리티를 사용하여 안전하게 URL을 가져옵니다.
            return s3Client.utilities().getUrl(builder -> builder.bucket(bucketName).key(fileName)).toExternalForm();
        } catch (IOException e) {
            log.error("S3 파일 업로드 실패: {}", fileName, e);
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    /**
     * S3에 저장될 파일 이름을 생성합니다. (예: dogs/user_1/dog_1/20250813_123456_abcdef.jpg)
     * @param folder "dogs" 또는 "profiles"
     * @param userId 사용자 ID
     * @param dogId 강아지 ID (프로필 이미지의 경우 null)
     * @param originalFileName 원본 파일 이름 (확장자 추출용)
     * @return S3에 저장될 전체 경로가 포함된 파일 이름
     */
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

    /**
     * 파일 이름에서 확장자를 추출합니다.
     * @param fileName 파일 이름
     * @return 확장자
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 전체 S3 URL에서 객체 키(파일 경로)를 추출합니다.
     * @param url 전체 S3 URL
     * @return 객체 키
     */
    private String extractKeyFromUrl(String url) {
        try {
            URL urlObj = new URL(url);
            // URL 경로의 첫 번째 '/'를 제거하여 키를 반환합니다. (예: /folder/file.jpg -> folder/file.jpg)
            return urlObj.getPath().substring(1);
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 S3 URL입니다: " + url);
        }
    }
}
