package com.example.dogmeeting.controller;

import com.example.dogmeeting.dto.UserProfileResponse;
import com.example.dogmeeting.dto.UserUpdateRequest;
import com.example.dogmeeting.dto.UserRankingResponse;
import com.example.dogmeeting.dto.DogRankingResponse;
import com.example.dogmeeting.dto.UserResponse;
import com.example.dogmeeting.entity.User;
import com.example.dogmeeting.service.UserService;
import com.example.dogmeeting.service.DogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final DogService dogService;

    /**
     * 사용자 프로필 조회 (홈 화면용)
     * GET /api/home/profile/{loginId}
     */
    @GetMapping("/profile/{loginId}")
    public ResponseEntity<UserProfileResponse> getUserProfileByLoginId(@PathVariable String loginId) {
        // loginId로 사용자 ID 조회 후 프로필 조회
        UserResponse userResponse = userService.getUserByLoginId(loginId);
        UserProfileResponse profile = userService.getUserProfile(userResponse.getId());
        return ResponseEntity.ok(profile);
    }

    /**
     * 사용자 프로필 수정 (연필 버튼)
     * PUT /api/home/profile/{loginId}
     */
    @PutMapping("/profile/{loginId}")
    public ResponseEntity<UserProfileResponse> updateUserProfileByLoginId(
            @PathVariable String loginId,
            @Valid @RequestBody UserUpdateRequest request) {
        // loginId로 사용자 ID 조회 후 프로필 수정
        UserResponse userResponse = userService.getUserByLoginId(loginId);
        UserProfileResponse updatedProfile = userService.updateUserProfile(userResponse.getId(), request);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * 사용자 랭킹 조회 (홈 화면 랭킹 섹션)
     * GET /api/home/ranking?page=0&size=10
     */
    @GetMapping("/ranking")
    public ResponseEntity<List<UserRankingResponse>> getUserRanking(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<UserRankingResponse> rankings = userService.getUserRanking(page, size);
        return ResponseEntity.ok(rankings);
    }

    /**
     * 지역별 강아지 랭킹 조회 (홈 화면 - 내 지역 TOP 3)
     * GET /api/home/regional-dogs/{city}
     */
    @GetMapping("/regional-dogs/{city}")
    public ResponseEntity<List<DogRankingResponse>> getTopDogsInRegion(
            @PathVariable String city,
            @RequestParam(defaultValue = "3") int limit) {
        List<DogRankingResponse> topDogs = dogService.getTopDogsInRegion(city, limit);
        return ResponseEntity.ok(topDogs);
    }

    /**
     * 전체 강아지 랭킹 조회 (홈 화면 랭킹 섹션)
     * GET /api/home/dog-ranking?page=0&size=10
     */
    @GetMapping("/dog-ranking")
    public ResponseEntity<List<DogRankingResponse>> getDogRanking(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<DogRankingResponse> rankings = dogService.getAllDogsRanking(page, size);
        return ResponseEntity.ok(rankings);
    }

    /**
     * 특정 사용자 상세 정보 조회 (랭킹에서 사용자 클릭 시)
     * GET /api/home/user/{loginId}
     */
    @GetMapping("/user/{loginId}")
    public ResponseEntity<UserProfileResponse> getUserDetailProfileByLoginId(@PathVariable String loginId) {
        UserResponse userResponse = userService.getUserByLoginId(loginId);
        UserProfileResponse userDetail = userService.getUserDetailProfile(userResponse.getId());
        return ResponseEntity.ok(userDetail);
    }
} 