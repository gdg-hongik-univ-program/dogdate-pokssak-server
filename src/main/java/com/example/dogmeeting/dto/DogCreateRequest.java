package com.example.dogmeeting.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DogCreateRequest {
    
    @NotBlank(message = "강아지 이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "견종을 입력해주세요.")
    private String breed;

    @NotNull(message = "나이를 입력해주세요.")
    @Min(value = 0, message = "나이는 0 이상이어야 합니다.")
    private Integer age;

    @NotBlank(message = "성별을 입력해주세요.")
    private String gender;

    private String description;

    private String photoUrl;
} 