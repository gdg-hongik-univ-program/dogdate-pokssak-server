package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.MatchResponse;
import com.example.dogmeeting.dto.SwipeResponse;

import java.util.List;

public interface SwipeService {
    MatchResponse swipeUser(Long fromUserId, Long toUserId);
    boolean hasAlreadySwiped(Long fromUserId, Long toUserId);
    
    // 좋아요 관련 메서드들
    boolean toggleLike(Long fromUserId, Long toUserId);
    boolean isLiked(Long fromUserId, Long toUserId);
    
    // 스와이프 목록 조회
    List<SwipeResponse> getSentSwipes(Long userId);
    List<SwipeResponse> getReceivedSwipes(Long userId);
} 