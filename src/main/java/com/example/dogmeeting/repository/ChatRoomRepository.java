package com.example.dogmeeting.repository;

import com.example.dogmeeting.entity.ChatRoom;
import com.example.dogmeeting.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    Optional<ChatRoom> findByMatch(Match match);
    
    @Query("SELECT c FROM ChatRoom c WHERE c.match.id = :matchId")
    ChatRoom findByMatchId(@Param("matchId") Long matchId);
} 