package com.example.dogmeeting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileResponse {
    private Long id;
    private String userId;
    private String nickname;
    private String gender;
    private String city;
    private String district;
    private LocalDateTime createdAt;
    private List<DogResponse> dogs;
    private int matchCount;          // 매칭 수
    private double averageRating;    // 평균 평점
    private int rankingScore;        // 랭킹 점수
} 