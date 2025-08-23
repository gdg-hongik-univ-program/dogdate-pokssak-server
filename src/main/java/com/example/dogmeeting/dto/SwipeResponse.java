package com.example.dogmeeting.dto;

import com.example.dogmeeting.entity.Swipe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SwipeResponse {
    
    private Long id;
    private Long fromUserId;
    private String fromUserNickname;
    private Long toUserId;
    private String toUserNickname;
    private Boolean isLike;
    private LocalDateTime swipedAt;
    private LocalDateTime likeAt;
    
    public static SwipeResponse from(Swipe swipe) {
        return SwipeResponse.builder()
                .id(swipe.getId())
                .fromUserId(swipe.getFromUser().getId())
                .fromUserNickname(swipe.getFromUser().getNickname())
                .toUserId(swipe.getToUser().getId())
                .toUserNickname(swipe.getToUser().getNickname())
                .isLike(swipe.getLike())
                .swipedAt(swipe.getSwipedAt())
                .likeAt(swipe.getLikeAt())
                .build();
    }
}