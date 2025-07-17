package com.example.dogmeeting.repository;

import com.example.dogmeeting.entity.Match;
import com.example.dogmeeting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    
    @Query("SELECT m FROM Match m WHERE (m.user1 = :user OR m.user2 = :user) AND m.status = :status")
    List<Match> findByUserAndStatus(@Param("user") User user, @Param("status") String status);
    
    @Query("SELECT m FROM Match m WHERE m.user1 = :user OR m.user2 = :user")
    List<Match> findByUser(@Param("user") User user);
    
    @Query("SELECT m FROM Match m WHERE " +
           "(m.user1.id = :user1Id AND m.user2.id = :user2Id) OR " +
           "(m.user1.id = :user2Id AND m.user2.id = :user1Id)")
    Optional<Match> findByUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);
    
    List<Match> findByStatus(String status);
} 