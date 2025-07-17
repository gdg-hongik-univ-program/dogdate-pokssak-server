package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.MatchResponse;

public interface SwipeService {
    MatchResponse swipeUser(Long fromUserId, Long toUserId);
    boolean hasAlreadySwiped(Long fromUserId, Long toUserId);
} 