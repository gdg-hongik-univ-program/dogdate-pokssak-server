package com.example.dogmeeting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRankingResponse {
    private Long id;
    private String nickname;
    private String city;
    private String district;
    private int matchCount;          // 매칭 수
    private double averageRating;    // 평균 평점
    private int rankingScore;        // 랭킹 점수
    private int rank;                // 순위
    private String mainDogPhotoUrl;  // 대표 강아지 사진
    private String mainDogName;      // 대표 강아지 이름
} 