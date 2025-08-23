package com.example.dogmeeting.dto;

import com.example.dogmeeting.entity.Match;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResponse {
    
    private Long id;
    private Long user1Id;
    private String user1Nickname;
    private Long user2Id;
    private String user2Nickname;
    private String status;
    private LocalDateTime createdAt;
    
    public static MatchResponse from(Match match) {
        return MatchResponse.builder()
                .id(match.getId())
                .user1Id(match.getUser1().getId())
                .user1Nickname(match.getUser1().getNickname())
                .user2Id(match.getUser2().getId())
                .user2Nickname(match.getUser2().getNickname())
                .status(match.getStatus())
                .createdAt(match.getCreatedAt())
                .build();
    }
} 