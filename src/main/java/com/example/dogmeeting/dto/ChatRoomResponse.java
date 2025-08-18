package com.example.dogmeeting.dto;

import com.example.dogmeeting.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponse {
    
    private Long id;
    private Long matchId;
    private LocalDateTime createdAt;
    
    public static ChatRoomResponse from(ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .matchId(chatRoom.getMatch().getId())
                .createdAt(chatRoom.getCreatedAt())
                .build();
    }
}