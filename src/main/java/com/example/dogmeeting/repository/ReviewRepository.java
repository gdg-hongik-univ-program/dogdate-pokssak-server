package com.example.dogmeeting.repository;

import com.example.dogmeeting.entity.Match;
import com.example.dogmeeting.entity.Review;
import com.example.dogmeeting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    Optional<Review> findByMatchAndReviewer(Match match, User reviewer);
    
    List<Review> findByMatch(Match match);
    
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r " +
           "JOIN r.match m WHERE m.user1.id = :userId OR m.user2.id = :userId")
    double findAverageRatingByUserId(@Param("userId") Long userId);
    
    List<Review> findByReviewer(User reviewer);
    
    @Query("SELECT AVG(r.rating) FROM Review r JOIN r.match m WHERE " +
           "(m.user1.id = :userId OR m.user2.id = :userId) AND r.reviewer.id != :userId")
    Double getAverageRatingForUser(@Param("userId") Long userId);
    
    @Query("SELECT r FROM Review r JOIN r.match m WHERE " +
           "(m.user1.id = :userId OR m.user2.id = :userId) AND r.reviewer.id != :userId")
    List<Review> findReviewsForUser(@Param("userId") Long userId);
} 