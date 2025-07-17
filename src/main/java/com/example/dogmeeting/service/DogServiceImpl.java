package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.DogCreateRequest;
import com.example.dogmeeting.dto.DogResponse;
import com.example.dogmeeting.entity.Dog;
import com.example.dogmeeting.entity.User;
import com.example.dogmeeting.exception.UserNotFoundException;
import com.example.dogmeeting.repository.DogRepository;
import com.example.dogmeeting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DogServiceImpl implements DogService {

    private final DogRepository dogRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Long createDog(Long userId, DogCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        Dog dog = Dog.builder()
                .user(user)
                .name(request.getName())
                .breed(request.getBreed())
                .age(request.getAge())
                .description(request.getDescription())
                .photoUrl(request.getPhotoUrl())
                .build();

        dogRepository.save(dog);
        return dog.getId();
    }

    @Override
    public DogResponse getDogById(Long dogId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new UserNotFoundException("강아지를 찾을 수 없습니다."));
        return DogResponse.from(dog);
    }

    @Override
    public List<DogResponse> getDogsByUserId(Long userId) {
        List<Dog> dogs = dogRepository.findByUserId(userId);
        return dogs.stream()
                .map(DogResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateDog(Long dogId, DogCreateRequest request) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new UserNotFoundException("강아지를 찾을 수 없습니다."));

        dog.updateInfo(request.getName(), request.getBreed(), request.getAge(),
                      request.getDescription(), request.getPhotoUrl());
    }

    @Override
    @Transactional
    public void updateDogImage(Long dogId, String imageUrl) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new UserNotFoundException("강아지를 찾을 수 없습니다."));
        
        // Dog 엔티티에 이미지 URL 업데이트 메서드 필요
        dog.updatePhotoUrl(imageUrl);
    }

    @Override
    @Transactional
    public void deleteDog(Long dogId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new UserNotFoundException("강아지를 찾을 수 없습니다."));
        dogRepository.delete(dog);
    }
} 