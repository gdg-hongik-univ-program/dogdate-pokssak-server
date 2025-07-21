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
    
    // 특정 사용자(강아지 소유자)가 받은 실제 좋아요 수 계산
    @Query("SELECT COUNT(s) FROM Swipe s WHERE s.toUser.id = :userId AND s.isLike = true")
    int countLikesByUserId(@Param("userId") Long userId);
    
    // 지역별 사용자들이 받은 좋아요 수 계산 (상위 3명)
    @Query("SELECT s.toUser.id, COUNT(s) as likeCount FROM Swipe s " +
           "WHERE s.toUser.city = :city AND s.isLike = true " +
           "GROUP BY s.toUser.id " +
           "ORDER BY likeCount DESC")
    List<Object[]> findTopUsersByLikesInCity(@Param("city") String city, org.springframework.data.domain.Pageable pageable);
    
    // 사용자별 좋아요 수 전체 랭킹 (페이지네이션)
    @Query("SELECT s.toUser.id, COUNT(s) as likeCount FROM Swipe s " +
           "WHERE s.isLike = true " +
           "GROUP BY s.toUser.id " +
           "ORDER BY likeCount DESC")
    List<Object[]> findAllUsersByLikes(org.springframework.data.domain.Pageable pageable);
    
    @Query("SELECT s FROM Swipe s WHERE s.fromUser.id = :fromUserId AND s.toUser.id = :toUserId")
    Optional<Swipe> findByFromUserIdAndToUserId(@Param("fromUserId") Long fromUserId, 
                                               @Param("toUserId") Long toUserId);
    
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Swipe s " +
           "WHERE s.fromUser.id = :fromUserId AND s.toUser.id = :toUserId")
    boolean existsByFromUserIdAndToUserId(@Param("fromUserId") Long fromUserId, 
                                         @Param("toUserId") Long toUserId);
} 