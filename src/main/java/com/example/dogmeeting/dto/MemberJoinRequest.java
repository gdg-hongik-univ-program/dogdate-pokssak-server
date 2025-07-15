package com.example.dogmeeting.dto;

import jakarta.validation.constraints.NotBlank; // 유효성 검사 어노테이션
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberJoinRequest {
    @NotBlank(message = "아이디")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    private String memberId;

    @NotBlank(message = "비밀번호")
    @Size(min = 4, message = "비밀번호는 4자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인")
    private String confirmPassword;

    @NotBlank(message = "닉네임")
    @Size(max = 50, message = "닉네임을 입력해주세요.")
    private String memberName;
}
