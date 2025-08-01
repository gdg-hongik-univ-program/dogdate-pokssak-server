package com.example.dogmeeting.repository;

import com.example.dogmeeting.entity.ChatRoom;
import com.example.dogmeeting.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    Optional<ChatRoom> findByMatch(Match match);
    
    ChatRoom findByMatchId(Long matchId);
} 