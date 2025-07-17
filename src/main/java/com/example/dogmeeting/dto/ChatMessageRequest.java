package com.example.dogmeeting.dto;

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
public class ChatMessageRequest {
    
    @NotNull(message = "채팅방 ID를 입력해주세요.")
    private Long chatroomId;

    @NotBlank(message = "메시지 내용을 입력해주세요.")
    private String content;
} 