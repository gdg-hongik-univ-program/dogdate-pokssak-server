package com.example.dogmeeting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDto {
    
    @NotNull(message = "채팅방 ID를 입력해주세요.")
    private Long chatroomId;

    @NotNull(message = "발신자 ID를 입력해주세요.")
    private Long senderId;

    @NotBlank(message = "메시지 내용을 입력해주세요.")
    private String content;

    @Builder.Default
    private MessageType type = MessageType.CHAT;

    private LocalDateTime timestamp;
    
    private String senderNickname;
    
    @Builder.Default
    private Boolean isRead = false;
}