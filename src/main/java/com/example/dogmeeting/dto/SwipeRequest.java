package com.example.dogmeeting.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SwipeRequest {
    
    @NotNull(message = "대상 사용자 ID를 입력해주세요.")
    private String toUserId;
} 