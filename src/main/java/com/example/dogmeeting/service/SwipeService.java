package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.MatchResponse;
import com.example.dogmeeting.dto.UserResponse;

import java.util.List;

public interface SwipeService {
    MatchResponse swipeUser(Long fromUserId, Long toUserId);
    boolean hasAlreadySwiped(Long fromUserId, Long toUserId);

    // 좋아요 관련 메서드들
    boolean toggleLike(Long fromUserId, Long toUserId);
    boolean isLiked(Long fromUserId, Long toUserId);

    List<UserResponse> getReceivedSwipes(Long userId);
} 