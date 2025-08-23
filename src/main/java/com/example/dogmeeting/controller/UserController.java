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
import java.util.Map;
import java.util.HashMap;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody UserJoinRequest request) {
        User newUser = userService.joinUser(request);
        UserResponse userResponse = UserResponse.from(newUser);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Long>> login(@Valid @RequestBody UserLoginRequest request) {
        // 1. 로그인 서비스를 호출하여 로그인된 User 객체를 받아옵니다.
        User loggedInUser = userService.loginUser(request.getUserId(), request.getPassword());

        // 2. 받아온 User 객체에서 숫자 ID(Long 타입)를 추출합니다.
        Long userId = loggedInUser.getId();

        // 3. Map 객체를 생성하여 userId를 JSON 형식으로 만듭니다.
        Map<String, Long> response = new HashMap<>();
        response.put("userId", userId);

        // 4. Map 객체를 ResponseEntity에 담아 성공(OK) 상태와 함께 반환합니다.
        // Spring Boot가 Map을 자동으로 {"userId": 1} 형태의 JSON으로 변환해줍니다.
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/nickname/{nickname}")
    public ResponseEntity<UserResponse> getUserByNickname(@PathVariable String nickname) {
        UserResponse user = userService.getUserByNickname(nickname);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{userId}/potential-matches")
    public ResponseEntity<List<UserResponse>> getPotentialMatches(@PathVariable Long userId) {
        List<UserResponse> matches = userService.getPotentialMatches(userId);
        return ResponseEntity.ok(matches);
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<String> updateProfile(
            @PathVariable Long userId,
            @RequestParam String nickname,
            @RequestParam String gender,
            @RequestParam String city,
            @RequestParam(required = false) String district) {
        userService.updateUserProfile(userId, nickname, gender, city, district);
        return ResponseEntity.ok("프로필이 성공적으로 업데이트되었습니다.");
    }
} 