package com.example.dogmeeting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @NotBlank(message = "닉네임은 필수입니다")
    private String nickname;

    @NotBlank(message = "성별은 필수입니다")
    @Pattern(regexp = "남성|여성", message = "성별은 '남성' 또는 '여성'이어야 합니다")
    private String gender;

    @NotBlank(message = "도시는 필수입니다")
    private String city;

    private String district;
} 