package com.example.dogmeeting.controller;

import com.example.dogmeeting.dto.DogCreateRequest;
import com.example.dogmeeting.dto.DogResponse;
import com.example.dogmeeting.dto.DogProfileResponse;
import com.example.dogmeeting.service.DogService;
import com.example.dogmeeting.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/dogs")
@RequiredArgsConstructor
public class DogController {

    private final DogService dogService;
    private final FileUploadService fileUploadService;

    @PostMapping("/users/{userId}")
    public ResponseEntity<String> createDog(
            @PathVariable Long userId,
            @Valid @RequestPart("dogInfo") DogCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        // ===== 디버깅 로그 추가 =====
        if (image == null) {
            log.info("### 이미지 파일이 NULL입니다.");
        } else if (image.isEmpty()) {
            log.info("### 이미지 파일이 비어있습니다 (Empty).");
            log.info("### 수신된 파일 이름: {}", image.getOriginalFilename());
        } else {
            log.info("### 이미지 파일 수신 성공!");
            log.info("### 원본 파일명: {}", image.getOriginalFilename());
            log.info("### 파일 크기: {} bytes", image.getSize());
            log.info("### Content Type: {}", image.getContentType());
        }
        // ===========================
        
        // 강아지 정보 먼저 저장
        Long dogId = dogService.createDog(userId, request);
        
        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = fileUploadService.uploadDogImage(image, userId, dogId);
                dogService.updateDogImage(dogId, imageUrl);
                return new ResponseEntity<>("강아지 정보가 성공적으로 등록되었습니다. ID: " + dogId + ", 이미지 URL: " + imageUrl, HttpStatus.CREATED);
            } catch (Exception e) {
                // S3 업로드 실패해도 강아지 정보는 저장됨
                return new ResponseEntity<>("강아지 정보가 등록되었지만 이미지 업로드에 실패했습니다. ID: " + dogId + ", 오류: " + e.getMessage(), HttpStatus.CREATED);
            }
        }
        
        return new ResponseEntity<>("강아지 정보가 성공적으로 등록되었습니다. ID: " + dogId, HttpStatus.CREATED);
    }

    @GetMapping("/{dogId}")
    public ResponseEntity<DogResponse> getDogById(@PathVariable Long dogId) {
        DogResponse dog = dogService.getDogById(dogId);
        return ResponseEntity.ok(dog);
    }

    /**
     * 강아지 상세 프로필 조회 (홈화면 랭킹에서 사진 클릭 시)
     * GET /api/dogs/{dogId}/profile
     */
    @GetMapping("/{dogId}/profile")
    public ResponseEntity<DogProfileResponse> getDogProfile(@PathVariable Long dogId) {
        DogProfileResponse dogProfile = dogService.getDogProfile(dogId);
        return ResponseEntity.ok(dogProfile);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<DogResponse>> getDogsByUserId(@PathVariable Long userId) {
        List<DogResponse> dogs = dogService.getDogsByUserId(userId);
        return ResponseEntity.ok(dogs);
    }

    @PutMapping("/{dogId}")
    public ResponseEntity<String> updateDog(
            @PathVariable Long dogId,
            @Valid @RequestPart("dogInfo") DogCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        dogService.updateDog(dogId, request, image);

        return ResponseEntity.ok("강아지 정보가 성공적으로 업데이트되었습니다.");
    }

    @DeleteMapping("/{dogId}")
    public ResponseEntity<String> deleteDog(@PathVariable Long dogId) {
        dogService.deleteDog(dogId);
        return ResponseEntity.ok("강아지 정보가 성공적으로 삭제되었습니다.");
    }


    // 강아지 이미지 업로드/변경

    @PostMapping("/{dogId}/image")
    public ResponseEntity<String> uploadDogImage(
            @PathVariable Long dogId,
            @RequestParam("userId") Long userId,
            @RequestPart("image") MultipartFile image) {
        
        String imageUrl = fileUploadService.uploadDogImage(image, userId, dogId);
        dogService.updateDogImage(dogId, imageUrl);
        
        return ResponseEntity.ok("강아지 사진이 성공적으로 업로드되었습니다. URL: " + imageUrl);
    }


    //강아지 이미지 삭제

    @DeleteMapping("/{dogId}/image")
    public ResponseEntity<String> deleteDogImage(@PathVariable Long dogId) {
        DogResponse dog = dogService.getDogById(dogId);
        if (dog.getPhotoUrl() != null) {
            fileUploadService.deleteFile(dog.getPhotoUrl());
            dogService.updateDogImage(dogId, null);
        }
        return ResponseEntity.ok("강아지 사진이 성공적으로 삭제되었습니다.");
    }
} 