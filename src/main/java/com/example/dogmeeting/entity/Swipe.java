package com.example.dogmeeting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "swipes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Swipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "swipe_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    @Column(name = "swiped_at", nullable = false)
    private LocalDateTime swipedAt;

    @Column(name = "is_like", nullable = false)
    @Builder.Default
    private Boolean isLike = false;  // 좋아요 여부

    @Column(name = "liked_at")
    private LocalDateTime likedAt;   // 좋아요 누른 시간 (선택적)

    @PrePersist
    protected void onCreate() {
        if (swipedAt == null) {
            swipedAt = LocalDateTime.now();
        }
    }
    
    // 좋아요 토글 메서드
    public void toggleLike() {
        this.isLike = !this.isLike;
        this.likedAt = this.isLike ? LocalDateTime.now() : null;
    }
    
    // 좋아요 설정 메서드
    public void setLike(boolean like) {
        this.isLike = like;
        this.likedAt = like ? LocalDateTime.now() : null;
    }
} 