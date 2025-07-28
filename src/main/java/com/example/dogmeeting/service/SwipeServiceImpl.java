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

import java.util.Optional;

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

    // 좋아요 관련 메서드들 구현
    @Override
    @Transactional
    public boolean toggleLike(Long fromUserId, Long toUserId) {
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        // 기존 스와이프가 있는지 확인
        Optional<Swipe> existingSwipe = swipeRepository.findByFromUserAndToUser(fromUser, toUser);
        
        if (existingSwipe.isPresent()) {
            // 기존 스와이프가 있으면 좋아요 상태만 토글
            Swipe swipe = existingSwipe.get();
            swipe.toggleLike();
            return swipe.getLike();
        } else {
            // 스와이프가 없으면 새로 생성하면서 좋아요 표시
            Swipe newSwipe = Swipe.builder()
                    .fromUser(fromUser)
                    .toUser(toUser)
                    .like(true)
                    .build();
            newSwipe.setLike(true); // 좋아요 시간 설정
            swipeRepository.save(newSwipe);
            return true;
        }
    }

    @Override
    public boolean isLiked(Long fromUserId, Long toUserId) {
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        Optional<Swipe> swipe = swipeRepository.findByFromUserAndToUser(fromUser, toUser);
        return swipe.map(Swipe::getLike).orElse(false);
    }
} 