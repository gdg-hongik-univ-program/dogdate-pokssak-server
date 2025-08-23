package com.example.dogmeeting.controller;

import com.example.dogmeeting.dto.MatchResponse;
import com.example.dogmeeting.dto.SwipeRequest;
import com.example.dogmeeting.dto.UserResponse;
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
        MatchResponse match = swipeService.swipeUser(fromUserId, request.getToUserId());
        
        if (match != null) {
            return ResponseEntity.ok(match);
        } else {
            return ResponseEntity.ok("스와이프가 완료되었습니다.");
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

    @GetMapping("/received/{userId}")
    public ResponseEntity<List<UserResponse>> getReceivedSwipes(@PathVariable Long userId) {
        List<UserResponse> receivedSwipes = swipeService.getReceivedSwipes(userId);
        return ResponseEntity.ok(receivedSwipes);
    }
} 