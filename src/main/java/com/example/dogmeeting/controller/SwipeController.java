package com.example.dogmeeting.controller;

import com.example.dogmeeting.dto.MatchResponse;
import com.example.dogmeeting.dto.SwipeRequest;
import com.example.dogmeeting.service.SwipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/swipes")
@RequiredArgsConstructor
public class SwipeController {

    private final SwipeService swipeService;

    @PostMapping("/users/{fromLoginId}")
    public ResponseEntity<?> swipeUser(
            @PathVariable String fromLoginId,
            @Valid @RequestBody SwipeRequest request) {
        MatchResponse match = swipeService.swipeUser(fromLoginId, request.getToUserId());
        
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
    @PostMapping("/like/{fromLoginId}/{toLoginId}")
    public ResponseEntity<?> toggleLike(
            @PathVariable String fromLoginId,
            @PathVariable String toLoginId) {
        boolean isLiked = swipeService.toggleLike(fromLoginId, toLoginId);
        
        String message = isLiked ? "좋아요를 눌렀습니다." : "좋아요를 취소했습니다.";
        return ResponseEntity.ok(message);
    }

    /**
     * 좋아요 상태 조회
     * GET /api/swipes/like-status/{fromUserId}/{toUserId}
     */
    @GetMapping("/like-status/{fromLoginId}/{toLoginId}")
    public ResponseEntity<Boolean> getLikeStatus(
            @PathVariable String fromLoginId,
            @PathVariable String toLoginId) {
        boolean isLiked = swipeService.isLiked(fromLoginId, toLoginId);
        return ResponseEntity.ok(isLiked);
    }

    @GetMapping("/users/{fromLoginId}/check/{toLoginId}")
    public ResponseEntity<Boolean> checkIfSwiped(
            @PathVariable String fromLoginId,
            @PathVariable String toLoginId) {
        boolean hasSwiped = swipeService.hasAlreadySwiped(fromLoginId, toLoginId);
        return ResponseEntity.ok(hasSwiped);
    }
} 