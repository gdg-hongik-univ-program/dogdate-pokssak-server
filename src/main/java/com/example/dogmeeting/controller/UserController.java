package com.example.dogmeeting.controller;

import com.example.dogmeeting.dto.UserJoinRequest;
import com.example.dogmeeting.dto.UserLoginRequest;
import com.example.dogmeeting.dto.UserResponse;
import com.example.dogmeeting.entity.User;
import com.example.dogmeeting.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserJoinRequest request) {
        userService.joinUser(request);
        return new ResponseEntity<>("회원가입이 성공적으로 완료되었습니다.", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginRequest request) {
        User loggedInUser = userService.loginUser(request.getUserId(), request.getPassword());
        return new ResponseEntity<>("로그인 성공! 환영합니다, " + loggedInUser.getNickname() + "님!", HttpStatus.OK);
    }

    @GetMapping("/{loginId}")
    public ResponseEntity<UserResponse> getUserByLoginId(@PathVariable String loginId) {
        UserResponse user = userService.getUserByLoginId(loginId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<UserResponse> getUserByNickname(@PathVariable String nickname) {
        UserResponse user = userService.getUserByNickname(nickname);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{loginId}/potential-matches")
    public ResponseEntity<List<UserResponse>> getPotentialMatches(@PathVariable String loginId) {
        List<UserResponse> matches = userService.getPotentialMatchesByLoginId(loginId);
        return ResponseEntity.ok(matches);
    }

    @PutMapping("/{loginId}/profile")
    public ResponseEntity<String> updateProfile(
            @PathVariable String loginId,
            @RequestParam String nickname,
            @RequestParam String gender,
            @RequestParam String city,
            @RequestParam(required = false) String district) {
        userService.updateUserProfileByLoginId(loginId, nickname, gender, city, district);
        return ResponseEntity.ok("프로필이 성공적으로 업데이트되었습니다.");
    }
} 