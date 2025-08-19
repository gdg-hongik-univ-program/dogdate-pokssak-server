package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.DogCreateRequest;
import com.example.dogmeeting.dto.DogResponse;
import com.example.dogmeeting.dto.DogRankingResponse;
import com.example.dogmeeting.dto.DogProfileResponse;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
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
    private final FileUploadService fileUploadService;

    private static final Duration PRESIGNED_URL_DURATION = Duration.ofHours(1); // Presigned URL 유효 시간

    @Override
    @Transactional
    public Long createDog(Long userId, DogCreateRequest request, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        Dog dog = Dog.builder()
                .user(user)
                .name(request.getName())
                .breed(request.getBreed())
                .age(request.getAge())
                .gender(request.getGender())
                .description(request.getDescription())
                .build(); // photoUrl은 여기서 설정하지 않고, 이미지 업로드 후 업데이트

        dogRepository.save(dog); // 먼저 강아지 정보 저장하여 dogId 확보

        if (image != null && !image.isEmpty()) {
            String imageKey = fileUploadService.uploadDogImage(image, userId, dog.getId());
            dog.updatePhotoUrl(imageKey); // 저장된 dogId를 사용하여 이미지 키 업데이트
        }

        return dog.getId();
    }

    @Override
    public DogResponse getDogById(Long dogId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new UserNotFoundException("강아지를 찾을 수 없습니다."));
        return toDogResponseWithPresignedUrl(dog);
    }

    @Override
    public DogProfileResponse getDogProfile(Long dogId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new UserNotFoundException("강아지를 찾을 수 없습니다."));
        return toDogProfileResponseWithPresignedUrl(dog);
    }

    @Override
    public List<DogResponse> getDogsByUserId(Long userId) {
        List<Dog> dogs = dogRepository.findByUserId(userId);
        return dogs.stream()
                .map(this::toDogResponseWithPresignedUrl)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateDog(Long dogId, DogCreateRequest request, MultipartFile image) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new UserNotFoundException("강아지를 찾을 수 없습니다."));

        // 강아지 정보 업데이트
        dog.updateInfo(request.getName(), request.getBreed(), request.getAge(),
                      request.getGender(), request.getDescription());

        // 이미지 파일이 제공된 경우 처리
        if (image != null && !image.isEmpty()) {
            // 기존 이미지가 있다면 삭제 (photoUrl은 이제 객체 키)
            if (dog.getPhotoUrl() != null && !dog.getPhotoUrl().isEmpty()) {
                fileUploadService.deleteFile(dog.getPhotoUrl());
            }
            // 새 이미지 업로드
            String imageKey = fileUploadService.uploadDogImage(image, dog.getUser().getId(), dogId);
            dog.updatePhotoUrl(imageKey);
        } else {
            // 이미지가 제공되지 않았을 때, 기존 이미지를 유지할지 여부는 정책에 따라 달라짐.
            // 여기서는 이미지가 없으면 기존 이미지를 유지하는 것으로 가정.
            // 만약 이미지를 삭제하고 싶다면 deleteDogImage 엔드포인트를 사용해야 함.
        }
    }

    @Override
    @Transactional
    public void updateDogImage(Long dogId, String imageKey) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new UserNotFoundException("강아지를 찾을 수 없습니다."));
        
        dog.updatePhotoUrl(imageKey);
    }

    @Override
    @Transactional
    public void deleteDog(Long dogId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new UserNotFoundException("강아지를 찾을 수 없습니다."));
        
        // 강아지 삭제 시 연결된 이미지도 S3에서 삭제
        if (dog.getPhotoUrl() != null && !dog.getPhotoUrl().isEmpty()) {
            fileUploadService.deleteFile(dog.getPhotoUrl());
        }
        dogRepository.delete(dog);
    }

    @Override
    @Transactional
    public void deleteDogImage(Long dogId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new UserNotFoundException("강아지를 찾을 수 없습니다."));

        if (dog.getPhotoUrl() != null && !dog.getPhotoUrl().isEmpty()) {
            fileUploadService.deleteFile(dog.getPhotoUrl());
            dog.updatePhotoUrl(null); // DB에서도 이미지 URL 제거
        }
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
                    
                    // DogRankingResponse에 Presigned URL 설정
                    String photoUrl = null;
                    if (mainDog.getPhotoUrl() != null && !mainDog.getPhotoUrl().isEmpty()) {
                        photoUrl = fileUploadService.generatePresignedGetUrl(mainDog.getPhotoUrl(), PRESIGNED_URL_DURATION);
                    }

                    return DogRankingResponse.builder()
                            .dogId(mainDog.getId())
                            .dogName(mainDog.getName())
                            .breed(mainDog.getBreed())
                            .age(mainDog.getAge())
                            .photoUrl(photoUrl) // Presigned URL 설정
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
                    
                    // DogRankingResponse에 Presigned URL 설정
                    String photoUrl = null;
                    if (mainDog.getPhotoUrl() != null && !mainDog.getPhotoUrl().isEmpty()) {
                        photoUrl = fileUploadService.generatePresignedGetUrl(mainDog.getPhotoUrl(), PRESIGNED_URL_DURATION);
                    }

                    return DogRankingResponse.builder()
                            .dogId(mainDog.getId())
                            .dogName(mainDog.getName())
                            .breed(mainDog.getBreed())
                            .age(mainDog.getAge())
                            .photoUrl(photoUrl) // Presigned URL 설정
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

    // 헬퍼 메소드: Dog 엔티티를 DogResponse DTO로 변환하며 Presigned URL 설정
    private DogResponse toDogResponseWithPresignedUrl(Dog dog) {
        String photoUrl = null;
        if (dog.getPhotoUrl() != null && !dog.getPhotoUrl().isEmpty()) {
            photoUrl = fileUploadService.generatePresignedGetUrl(dog.getPhotoUrl(), PRESIGNED_URL_DURATION);
        }
        return DogResponse.builder()
                .id(dog.getId())
                .name(dog.getName())
                .breed(dog.getBreed())
                .age(dog.getAge())
                .gender(dog.getGender()) // Presigned URL 설정
                .description(dog.getDescription())
                .photoUrl(photoUrl) // Presigned URL 설정
                .build();
    }

    // 헬퍼 메소드: Dog 엔티티를 DogProfileResponse DTO로 변환하며 Presigned URL 설정
    private DogProfileResponse toDogProfileResponseWithPresignedUrl(Dog dog) {
        User owner = dog.getUser();
        int likeCount = swipeRepository.countByToUserAndLike(owner, true);
        int rank = 0; // 임시
        List<String> titles = List.of(); // 임시

        String photoUrl = null;
        if (dog.getPhotoUrl() != null && !dog.getPhotoUrl().isEmpty()) {
            photoUrl = fileUploadService.generatePresignedGetUrl(dog.getPhotoUrl(), PRESIGNED_URL_DURATION);
        }

        return DogProfileResponse.builder()
                .dogId(dog.getId())
                .name(dog.getName())
                .breed(dog.getBreed())
                .age(dog.getAge())
                .description(dog.getDescription())
                .photoUrl(photoUrl) // Presigned URL 설정
                .ownerId(owner.getId())
                .ownerNickname(owner.getNickname())
                .ownerGender(owner.getGender())
                .ownerCity(owner.getCity())
                .ownerDistrict(owner.getDistrict())
                .likeCount(likeCount)
                .rank(rank)
                .titles(titles)
                .build();
    }
}  