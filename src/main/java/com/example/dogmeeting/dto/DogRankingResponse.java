package com.example.dogmeeting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DogRankingResponse {
    private Long dogId;
    private String dogName;
    private String breed;
    private int age;
    private String photoUrl;
    private String description;
    
    // 소유자 정보
    private Long ownerId;
    private String ownerNickname;
    private String ownerCity;
    private String ownerDistrict;
    
    // 랭킹 정보
    private int likeCount;          // 좋아요 수
    private int rank;               // 순위
} 