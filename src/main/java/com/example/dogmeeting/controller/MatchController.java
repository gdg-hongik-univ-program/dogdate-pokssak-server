package com.example.dogmeeting.controller;

import com.example.dogmeeting.dto.MatchResponse;
import com.example.dogmeeting.entity.Match;
import com.example.dogmeeting.entity.User;
import com.example.dogmeeting.exception.UserNotFoundException;
import com.example.dogmeeting.repository.MatchRepository;
import com.example.dogmeeting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    @GetMapping("/users/{loginId}")
    public ResponseEntity<List<MatchResponse>> getUserMatches(@PathVariable String loginId) {
        User user = userRepository.findByUserId(loginId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + loginId));

        List<Match> matches = matchRepository.findByUser(user);
        List<MatchResponse> matchResponses = matches.stream()
                .map(MatchResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(matchResponses);
    }

    @GetMapping("/users/{loginId}/active")
    public ResponseEntity<List<MatchResponse>> getActiveMatches(@PathVariable String loginId) {
        User user = userRepository.findByUserId(loginId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + loginId));

        List<Match> matches = matchRepository.findByUserAndStatus(user, "ACTIVE");
        List<MatchResponse> matchResponses = matches.stream()
                .map(MatchResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(matchResponses);
    }

    @PutMapping("/{matchId}/status")
    public ResponseEntity<String> updateMatchStatus(
            @PathVariable Long matchId,
            @RequestParam String status) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new UserNotFoundException("매칭을 찾을 수 없습니다."));

        match.updateStatus(status);
        matchRepository.save(match);

        return ResponseEntity.ok("매칭 상태가 업데이트되었습니다.");
    }
} 