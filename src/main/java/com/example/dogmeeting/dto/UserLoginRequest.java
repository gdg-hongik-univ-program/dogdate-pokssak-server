package com.example.dogmeeting.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequest {
    
    @NotBlank(message = "아이디를 입력해주세요.")
    private String userId;  // 로그인용 아이디

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
} 