package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.DogCreateRequest;
import com.example.dogmeeting.dto.DogResponse;

import java.util.List;

public interface DogService {
    Long createDog(Long userId, DogCreateRequest request);
    DogResponse getDogById(Long dogId);
    List<DogResponse> getDogsByUserId(Long userId);
    void updateDog(Long dogId, DogCreateRequest request);
    void updateDogImage(Long dogId, String imageUrl);
    void deleteDog(Long dogId);
} 