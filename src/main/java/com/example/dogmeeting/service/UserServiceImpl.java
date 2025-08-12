package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.UserJoinRequest;
import com.example.dogmeeting.dto.UserResponse;
import com.example.dogmeeting.dto.UserProfileResponse;
import com.example.dogmeeting.dto.UserUpdateRequest;
import com.example.dogmeeting.dto.UserRankingResponse;
import com.example.dogmeeting.dto.DogResponse;
import com.example.dogmeeting.entity.User;
import com.example.dogmeeting.exception.DuplicateNicknameException;
import com.example.dogmeeting.exception.UserNotFoundException;
import com.example.dogmeeting.exception.PasswordMismatchException;
import com.example.dogmeeting.exception.DuplicateUserIdException;
import com.example.dogmeeting.repository.UserRepository;
import com.example.dogmeeting.repository.MatchRepository;
import com.example.dogmeeting.repository.SwipeRepository;
import com.example.dogmeeting.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegionService regionService;
    private final MatchRepository matchRepository;
    private final SwipeRepository swipeRepository;

    @Override
    @Transactional
    public Long joinUser(UserJoinRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        // userId 중복 체크
        userRepository.findByUserId(request.getUserId())
                .ifPresent(u -> {
                    throw new DuplicateUserIdException("이미 사용 중인 아이디입니다.");
                });

        // nickname 중복 체크
        userRepository.findByNickname(request.getNickname())
                .ifPresent(u -> {
                    throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다.");
                });

        User newUser = User.builder()
                .userId(request.getUserId())
                .nickname(request.getNickname())
                .password(request.getPassword())
                .gender(request.getGender())
                .city(request.getCity())
                .district(request.getDistrict())
                .build();
        
        newUser.encryptPassword(passwordEncoder);
        userRepository.save(newUser);
        
        return newUser.getId();
    }

    @Override
    public User loginUser(String userId, String password) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("아이디를 찾을 수 없습니다."));

        if (!user.checkPassword(passwordEncoder, password)) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }
        
        return user;
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        return UserResponse.from(user);
    }

    @Override
    public UserResponse getUserByLoginId(String loginId) {
        User user = userRepository.findByUserId(loginId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + loginId));
        return UserResponse.from(user);
    }

    @Override
    public UserResponse getUserByNickname(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        return UserResponse.from(user);
    }

    @Override
    public List<UserResponse> getPotentialMatches(Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        
        List<User> potentialMatches;
        
        // district가 있으면 같은 district 내에서, 없으면 같은 city 내에서 매칭
        if (currentUser.getDistrict() != null && !currentUser.getDistrict().trim().isEmpty()) {
            potentialMatches = userRepository.findPotentialMatchesInDistrict(
                    currentUser.getCity(), currentUser.getDistrict(), currentUser.getGender(), userId);
        } else {
            potentialMatches = userRepository.findPotentialMatches(
                    currentUser.getCity(), currentUser.getGender(), userId);
        }
        
        return potentialMatches.stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getPotentialMatchesByLoginId(String loginId) {
        User currentUser = userRepository.findByUserId(loginId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + loginId));
        
        List<User> potentialMatches;
        
        // district가 있으면 같은 district 내에서, 없으면 같은 city 내에서 매칭
        if (currentUser.getDistrict() != null && !currentUser.getDistrict().trim().isEmpty()) {
            potentialMatches = userRepository.findPotentialMatchesInDistrict(
                    currentUser.getCity(), currentUser.getDistrict(), currentUser.getGender(), currentUser.getId());
        } else {
            potentialMatches = userRepository.findPotentialMatches(
                    currentUser.getCity(), currentUser.getGender(), currentUser.getId());
        }
        
        return potentialMatches.stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateUserProfile(Long userId, String nickname, String gender, String city, String district) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        
        user.updateProfile(nickname, gender, city, district);
    }

    @Override
    @Transactional
    public void updateUserProfileByLoginId(String loginId, String nickname, String gender, String city, String district) {
        User user = userRepository.findByUserId(loginId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + loginId));
        
        user.updateProfile(nickname, gender, city, district);
    }

    // 홈 화면용 메서드들 구현
    @Override
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        
        // 강아지 정보 가져오기
        List<DogResponse> dogs = user.getDogs().stream()
                .map(DogResponse::from)
                .collect(Collectors.toList());
        
        // 매칭 수 계산
        int matchCount = matchRepository.countByUser1IdOrUser2Id(userId, userId);
        
        // 좋아요 수 계산 (새로운 랭킹 기준)
        int likeCount = swipeRepository.countLikesByUserId(userId);
        
        // 랭킹 점수 계산 (좋아요 수 기반)
        int rankingScore = likeCount;  // 좋아요 수가 곧 랭킹 점수
        
        return UserProfileResponse.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .city(user.getCity())
                .district(user.getDistrict())
                .createdAt(user.getCreatedAt())
                .dogs(dogs)
                .matchCount(matchCount)
                .rankingScore(rankingScore)
                .build();
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        
        user.updateProfile(request.getNickname(), request.getGender(), 
                          request.getCity(), request.getDistrict());
        
        return getUserProfile(userId);
    }

    @Override
    public List<UserRankingResponse> getUserRanking(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<User> users = userRepository.findAll(pageable).getContent();
        
        List<UserRankingResponse> rankings = users.stream()
                .map(user -> {
                    int matchCount = matchRepository.countByUser1IdOrUser2Id(user.getId(), user.getId());
                    int likeCount = swipeRepository.countLikesByUserId(user.getId());
                    int rankingScore = likeCount;  // 좋아요 수가 곧 랭킹 점수
                    
                    // 대표 강아지 정보
                    String mainDogPhotoUrl = user.getDogs().isEmpty() ? null : user.getDogs().get(0).getPhotoUrl();
                    String mainDogName = user.getDogs().isEmpty() ? null : user.getDogs().get(0).getName();
                    
                    return UserRankingResponse.builder()
                            .id(user.getId())
                            .nickname(user.getNickname())
                            .city(user.getCity())
                            .district(user.getDistrict())
                            .matchCount(matchCount)
                            .rankingScore(rankingScore)
                            .mainDogPhotoUrl(mainDogPhotoUrl)
                            .mainDogName(mainDogName)
                            .build();
                })
                .sorted((a, b) -> Integer.compare(b.getRankingScore(), a.getRankingScore()))
                .collect(Collectors.toList());
        
        // 순위 부여
        return IntStream.range(0, rankings.size())
                .mapToObj(i -> UserRankingResponse.builder()
                        .id(rankings.get(i).getId())
                        .nickname(rankings.get(i).getNickname())
                        .city(rankings.get(i).getCity())
                        .district(rankings.get(i).getDistrict())
                        .matchCount(rankings.get(i).getMatchCount())
                        .rankingScore(rankings.get(i).getRankingScore())
                        .rank(i + 1 + (page * size))
                        .mainDogPhotoUrl(rankings.get(i).getMainDogPhotoUrl())
                        .mainDogName(rankings.get(i).getMainDogName())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public UserProfileResponse getUserDetailProfile(Long targetUserId) {
        return getUserProfile(targetUserId);
    }
} 