package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.DogCreateRequest;
import com.example.dogmeeting.dto.DogResponse;
import com.example.dogmeeting.dto.DogRankingResponse;

import java.util.List;

public interface DogService {
    Long createDog(Long userId, DogCreateRequest request);
    DogResponse getDogById(Long dogId);
    List<DogResponse> getDogsByUserId(Long userId);
    void updateDog(Long dogId, DogCreateRequest request);
    void updateDogImage(Long dogId, String imageUrl);
    void deleteDog(Long dogId);
    
    // 랭킹 관련 메서드들
    List<DogRankingResponse> getTopDogsInRegion(String city, int limit);
    List<DogRankingResponse> getAllDogsRanking(int page, int size);
} 