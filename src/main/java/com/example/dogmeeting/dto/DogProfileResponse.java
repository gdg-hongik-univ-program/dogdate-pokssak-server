package com.example.dogmeeting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DogProfileResponse {
    
    // 강아지 기본 정보
    private Long dogId;
    private String name;
    private String breed;
    private Integer age;
    private String description;
    private String photoUrl;
    
    // 소유자 정보
    private Long ownerId;
    private String ownerNickname;
    private String ownerGender;
    private String ownerCity;
    private String ownerDistrict;
    
    // 소셜 정보
    private int likeCount;              // 받은 좋아요 수
    private int rank;                   // 현재 순위 (좋아요 수 기준)
    
    // 칭호/뱃지 정보
    private List<String> titles;        // 보유 칭호 목록
}