package com.example.dogmeeting.repository;

import com.example.dogmeeting.entity.Swipe;
import com.example.dogmeeting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SwipeRepository extends JpaRepository<Swipe, Long> {
    
    Optional<Swipe> findByFromUserAndToUser(User fromUser, User toUser);
    
    List<Swipe> findByFromUser(User fromUser);
    
    List<Swipe> findByToUser(User toUser);
    
    @Query("SELECT s FROM Swipe s WHERE s.fromUser.id = :fromUserId AND s.toUser.id = :toUserId")
    Optional<Swipe> findByFromUserIdAndToUserId(@Param("fromUserId") Long fromUserId, 
                                               @Param("toUserId") Long toUserId);
    
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Swipe s " +
           "WHERE s.fromUser.id = :fromUserId AND s.toUser.id = :toUserId")
    boolean existsByFromUserIdAndToUserId(@Param("fromUserId") Long fromUserId, 
                                         @Param("toUserId") Long toUserId);
} 