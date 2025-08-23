package com.example.dogmeeting.controller;

import com.example.dogmeeting.dto.MatchResponse;
import com.example.dogmeeting.dto.SwipeRequest;
import com.example.dogmeeting.dto.SwipeResponse;
import com.example.dogmeeting.service.SwipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/swipes")
@RequiredArgsConstructor
public class SwipeController {

    private final SwipeService swipeService;

    @PostMapping("/users/{fromUserId}")
    public ResponseEntity<?> swipeUser(
            @PathVariable Long fromUserId,
            @Valid @RequestBody SwipeRequest request) {
        try {
            MatchResponse match = swipeService.swipeUser(fromUserId, request.getToUserId());
            
            if (match != null) {
                return ResponseEntity.ok(match);
            } else {
                return ResponseEntity.ok("스와이프가 완료되었습니다.");
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 좋아요 토글 (스와이프와 독립적)
     * POST /api/swipes/like/{fromUserId}/{toUserId}
     */
    @PostMapping("/like/{fromUserId}/{toUserId}")
    public ResponseEntity<?> toggleLike(
            @PathVariable Long fromUserId,
            @PathVariable Long toUserId) {
        boolean isLiked = swipeService.toggleLike(fromUserId, toUserId);
        
        String message = isLiked ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.";
        return ResponseEntity.ok(message);
    }

    /**
     * 좋아요 상태 조회
     * GET /api/swipes/like-status/{fromUserId}/{toUserId}
     */
    @GetMapping("/like-status/{fromUserId}/{toUserId}")
    public ResponseEntity<Boolean> getLikeStatus(
            @PathVariable Long fromUserId,
            @PathVariable Long toUserId) {
        boolean isLiked = swipeService.isLiked(fromUserId, toUserId);
        return ResponseEntity.ok(isLiked);
    }

    @GetMapping("/users/{fromUserId}/check/{toUserId}")
    public ResponseEntity<Boolean> checkIfSwiped(
            @PathVariable Long fromUserId,
            @PathVariable Long toUserId) {
        boolean hasSwiped = swipeService.hasAlreadySwiped(fromUserId, toUserId);
        return ResponseEntity.ok(hasSwiped);
    }

    /**
     * 내가 스와이프한 목록 조회
     * GET /api/swipes/sent/{userId}
     */
    @GetMapping("/sent/{userId}")
    public ResponseEntity<List<SwipeResponse>> getSentSwipes(@PathVariable Long userId) {
        List<SwipeResponse> sentSwipes = swipeService.getSentSwipes(userId);
        return ResponseEntity.ok(sentSwipes);
    }

    /**
     * 내가 받은 스와이프 목록 조회
     * GET /api/swipes/received/{userId}
     */
    @GetMapping("/received/{userId}")
    public ResponseEntity<List<SwipeResponse>> getReceivedSwipes(@PathVariable Long userId) {
        List<SwipeResponse> receivedSwipes = swipeService.getReceivedSwipes(userId);
        return ResponseEntity.ok(receivedSwipes);
    }
} 