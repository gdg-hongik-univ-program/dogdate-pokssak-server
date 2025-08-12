package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.UserJoinRequest;
import com.example.dogmeeting.dto.UserResponse;
import com.example.dogmeeting.dto.UserProfileResponse;
import com.example.dogmeeting.dto.UserUpdateRequest;
import com.example.dogmeeting.dto.UserRankingResponse;
import com.example.dogmeeting.entity.User;

import java.util.List;

public interface UserService {
    Long joinUser(UserJoinRequest request);
    User loginUser(String userId, String password);
    UserResponse getUserById(Long userId);
    UserResponse getUserByLoginId(String loginId);
    UserResponse getUserByNickname(String nickname);
    List<UserResponse> getPotentialMatches(Long userId);
    List<UserResponse> getPotentialMatchesByLoginId(String loginId);
    void updateUserProfile(Long userId, String nickname, String gender, String city, String district);
    void updateUserProfileByLoginId(String loginId, String nickname, String gender, String city, String district);
    
    // 홈 화면용 메서드들
    UserProfileResponse getUserProfile(Long userId);
    UserProfileResponse updateUserProfile(Long userId, UserUpdateRequest request);
    List<UserRankingResponse> getUserRanking(int page, int size);
    UserProfileResponse getUserDetailProfile(Long targetUserId);
} 