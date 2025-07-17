package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.MatchResponse;
import com.example.dogmeeting.entity.Match;
import com.example.dogmeeting.entity.Swipe;
import com.example.dogmeeting.entity.User;
import com.example.dogmeeting.exception.UserNotFoundException;
import com.example.dogmeeting.repository.MatchRepository;
import com.example.dogmeeting.repository.SwipeRepository;
import com.example.dogmeeting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SwipeServiceImpl implements SwipeService {

    private final SwipeRepository swipeRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    @Override
    @Transactional
    public MatchResponse swipeUser(Long fromUserId, Long toUserId) {
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new UserNotFoundException("대상 사용자를 찾을 수 없습니다."));

        // 이미 스와이프했는지 확인
        if (swipeRepository.existsByFromUserIdAndToUserId(fromUserId, toUserId)) {
            throw new IllegalStateException("이미 스와이프한 사용자입니다.");
        }

        // 스와이프 기록 저장
        Swipe swipe = Swipe.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .build();
        swipeRepository.save(swipe);

        // 상대방도 나를 스와이프했는지 확인
        boolean mutualSwipe = swipeRepository.existsByFromUserIdAndToUserId(toUserId, fromUserId);
        
        if (mutualSwipe) {
            // 매칭 생성
            Match match = Match.builder()
                    .user1(fromUser)
                    .user2(toUser)
                    .status("ACTIVE")
                    .build();
            matchRepository.save(match);
            
            return MatchResponse.from(match);
        }
        
        return null; // 매칭되지 않음
    }

    @Override
    public boolean hasAlreadySwiped(Long fromUserId, Long toUserId) {
        return swipeRepository.existsByFromUserIdAndToUserId(fromUserId, toUserId);
    }
} 