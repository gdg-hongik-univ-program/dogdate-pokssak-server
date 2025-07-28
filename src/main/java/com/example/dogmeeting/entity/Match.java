package com.example.dogmeeting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "matches")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRooms;


    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    // 매칭에 참여한 사용자인지 확인하는 헬퍼 메서드
    public boolean isParticipant(Long userId) {
        return user1.getId().equals(userId) || user2.getId().equals(userId);
    }

    // 상대방 사용자를 반환하는 헬퍼 메서드
    public User getOtherUser(Long userId) {
        if (user1.getId().equals(userId)) {
            return user2;
        } else if (user2.getId().equals(userId)) {
            return user1;
        }
        return null;
    }
} 