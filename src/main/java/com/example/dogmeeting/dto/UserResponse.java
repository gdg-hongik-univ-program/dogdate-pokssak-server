package com.example.dogmeeting.dto;

import com.example.dogmeeting.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    
    private Long id;
    private String nickname;
    private String gender;
    private String city;
    private String district;
    private LocalDateTime createdAt;
    
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .city(user.getCity())
                .district(user.getDistrict())
                .createdAt(user.getCreatedAt())
                .build();
    }

    // 전체 지역 정보를 반환하는 헬퍼 메서드
    public String getFullRegion() {
        if (district != null && !district.trim().isEmpty()) {
            return city + " " + district;
        }
        return city;
    }
} 