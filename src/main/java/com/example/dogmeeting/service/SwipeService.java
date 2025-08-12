package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.MatchResponse;

public interface SwipeService {
    MatchResponse swipeUser(String fromLoginId, String toLoginId);
    boolean hasAlreadySwiped(String fromLoginId, String toLoginId);
    
    // 좋아요 관련 메서드들
    boolean toggleLike(String fromLoginId, String toLoginId);
    boolean isLiked(String fromLoginId, String toLoginId);
} 