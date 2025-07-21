package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.DogCreateRequest;
import com.example.dogmeeting.dto.DogResponse;
import com.example.dogmeeting.dto.DogRankingResponse;
import com.example.dogmeeting.entity.Dog;
import com.example.dogmeeting.entity.User;
import com.example.dogmeeting.exception.UserNotFoundException;
import com.example.dogmeeting.repository.DogRepository;
import com.example.dogmeeting.repository.UserRepository;
import com.example.dogmeeting.repository.SwipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DogServiceImpl implements DogService {

    private final DogRepository dogRepository;
    private final UserRepository userRepository;
    private final SwipeRepository swipeRepository;

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

    // 랭킹 관련 메서드들 구현
    @Override
    public List<DogRankingResponse> getTopDogsInRegion(String city, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Object[]> topUsers = swipeRepository.findTopUsersByLikesInCity(city, pageable);
        
        return topUsers.stream()
                .map(result -> {
                    Long userId = (Long) result[0];
                    Long likeCount = (Long) result[1];
                    
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
                    
                    // 사용자의 대표 강아지 (첫 번째 강아지)
                    if (user.getDogs().isEmpty()) {
                        return null; // 강아지가 없는 사용자는 제외
                    }
                    
                    Dog mainDog = user.getDogs().get(0);
                    
                    return DogRankingResponse.builder()
                            .dogId(mainDog.getId())
                            .dogName(mainDog.getName())
                            .breed(mainDog.getBreed())
                            .age(mainDog.getAge())
                            .photoUrl(mainDog.getPhotoUrl())
                            .description(mainDog.getDescription())
                            .ownerId(user.getId())
                            .ownerNickname(user.getNickname())
                            .ownerCity(user.getCity())
                            .ownerDistrict(user.getDistrict())
                            .likeCount(likeCount.intValue())
                            .rank(0) // 순위는 나중에 설정
                            .build();
                })
                .filter(response -> response != null) // null 제외
                .collect(Collectors.toList());
    }

    @Override
    public List<DogRankingResponse> getAllDogsRanking(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Object[]> allUsers = swipeRepository.findAllUsersByLikes(pageable);
        
        List<DogRankingResponse> rankings = allUsers.stream()
                .map(result -> {
                    Long userId = (Long) result[0];
                    Long likeCount = (Long) result[1];
                    
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
                    
                    // 사용자의 대표 강아지 (첫 번째 강아지)
                    if (user.getDogs().isEmpty()) {
                        return null; // 강아지가 없는 사용자는 제외
                    }
                    
                    Dog mainDog = user.getDogs().get(0);
                    
                    return DogRankingResponse.builder()
                            .dogId(mainDog.getId())
                            .dogName(mainDog.getName())
                            .breed(mainDog.getBreed())
                            .age(mainDog.getAge())
                            .photoUrl(mainDog.getPhotoUrl())
                            .description(mainDog.getDescription())
                            .ownerId(user.getId())
                            .ownerNickname(user.getNickname())
                            .ownerCity(user.getCity())
                            .ownerDistrict(user.getDistrict())
                            .likeCount(likeCount.intValue())
                            .rank(0) // 순위는 나중에 설정
                            .build();
                })
                .filter(response -> response != null) // null 제외
                .collect(Collectors.toList());
        
        // 순위 부여 (페이지 offset 포함)
        return IntStream.range(0, rankings.size())
                .mapToObj(i -> DogRankingResponse.builder()
                        .dogId(rankings.get(i).getDogId())
                        .dogName(rankings.get(i).getDogName())
                        .breed(rankings.get(i).getBreed())
                        .age(rankings.get(i).getAge())
                        .photoUrl(rankings.get(i).getPhotoUrl())
                        .description(rankings.get(i).getDescription())
                        .ownerId(rankings.get(i).getOwnerId())
                        .ownerNickname(rankings.get(i).getOwnerNickname())
                        .ownerCity(rankings.get(i).getOwnerCity())
                        .ownerDistrict(rankings.get(i).getOwnerDistrict())
                        .likeCount(rankings.get(i).getLikeCount())
                        .rank(i + 1 + (page * size))
                        .build())
                .collect(Collectors.toList());
    }
} 