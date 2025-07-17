package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.UserJoinRequest;
import com.example.dogmeeting.dto.UserResponse;
import com.example.dogmeeting.entity.User;

import java.util.List;

public interface UserService {
    Long joinUser(UserJoinRequest request);
    User loginUser(String nickname, String password);
    UserResponse getUserById(Long userId);
    UserResponse getUserByNickname(String nickname);
    List<UserResponse> getPotentialMatches(Long userId);
    void updateUserProfile(Long userId, String nickname, String gender, String city, String district);
} 