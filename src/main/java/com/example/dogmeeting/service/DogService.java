package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.DogCreateRequest;
import com.example.dogmeeting.dto.DogResponse;
import com.example.dogmeeting.dto.DogRankingResponse;
import com.example.dogmeeting.dto.DogProfileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DogService {
    Long createDog(Long userId, DogCreateRequest request, MultipartFile image);
    DogResponse getDogById(Long dogId);
    DogProfileResponse getDogProfile(Long dogId);
    List<DogResponse> getDogsByUserId(Long userId);
    void updateDog(Long dogId, DogCreateRequest request, org.springframework.web.multipart.MultipartFile image);
    void updateDogImage(Long dogId, String imageKey);
    void deleteDog(Long dogId);
    void deleteDogImage(Long dogId);
    
    // 랭킹 관련 메서드들
    List<DogRankingResponse> getTopDogsInRegion(String city, int limit);
    List<DogRankingResponse> getAllDogsRanking(int page, int size);
} 