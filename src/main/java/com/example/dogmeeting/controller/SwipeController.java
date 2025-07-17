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

    @GetMapping("/users/{fromUserId}/check/{toUserId}")
    public ResponseEntity<Boolean> checkIfSwiped(
            @PathVariable Long fromUserId,
            @PathVariable Long toUserId) {
        boolean hasSwiped = swipeService.hasAlreadySwiped(fromUserId, toUserId);
        return ResponseEntity.ok(hasSwiped);
    }
} 