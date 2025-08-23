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
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/dogs")
@RequiredArgsConstructor
public class DogController {

    private final DogService dogService;
    private final FileUploadService fileUploadService; // This is needed for uploadDogImage

    @PostMapping("/users/{userId}")
    public ResponseEntity<Map<String, Long>> createDog(
            @PathVariable Long userId,
            @Valid @RequestPart("dogInfo") DogCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        Long dogId = dogService.createDog(userId, request, image);
        return new ResponseEntity<>(Map.of("dogId", dogId), HttpStatus.CREATED);
    }

    @GetMapping("/{dogId}")
    public ResponseEntity<DogResponse> getDogById(@PathVariable Long dogId) {
        DogResponse dog = dogService.getDogById(dogId);
        return ResponseEntity.ok(dog);
    }

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
        
        String imageKey = fileUploadService.uploadDogImage(image, userId, dogId);
        dogService.updateDogImage(dogId, imageKey);
        
        return ResponseEntity.ok("강아지 사진이 성공적으로 업로드되었습니다.");
    }


    //강아지 이미지 삭제
    @DeleteMapping("/{dogId}/image")
    public ResponseEntity<String> deleteDogImage(@PathVariable Long dogId) {
        dogService.deleteDogImage(dogId);
        return ResponseEntity.ok("강아지 사진이 성공적으로 삭제되었습니다.");
    }
} 