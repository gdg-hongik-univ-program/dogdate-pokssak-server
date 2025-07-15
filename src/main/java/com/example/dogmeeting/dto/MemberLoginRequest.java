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
public class MemberLoginRequest {
    @NotBlank(message = "아이디")
    private String memberId;

    @NotBlank(message = "비밀번호")
    private String password;
}
