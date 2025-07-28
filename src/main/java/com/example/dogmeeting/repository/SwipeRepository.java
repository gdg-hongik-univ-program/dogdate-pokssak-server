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
    @Query("SELECT COUNT(s) FROM Swipe s WHERE s.toUser.id = :userId AND s.like = true")
    int countLikesByUserId(@Param("userId") Long userId);
    
    // 지역별 사용자들이 받은 좋아요 수 계산 (상위 3명)
    @Query("SELECT s.toUser.id, COUNT(s) as likeCount FROM Swipe s " +
           "WHERE s.toUser.city = :city AND s.like = true " +
           "GROUP BY s.toUser.id " +
           "ORDER BY likeCount DESC")
    List<Object[]> findTopUsersByLikesInCity(@Param("city") String city, org.springframework.data.domain.Pageable pageable);
    //짜친다너무 최신 jpa에서 지원하는 프로젝션을 쓰는데 select에서 받을 것들 그냥 받으면 object[]로 받는데 범용적으로 쓰기 어렵다
    // 객체를 받아야 쓸 수 있는데;;  projection으로 하면 dto에 객체 넣고 Object를 dto로 바꿔주면 재활용이 가능
    
    // 사용자별 좋아요 수 전체 랭킹 (페이지네이션)
    @Query("SELECT s.toUser.id, COUNT(s) as likeCount FROM Swipe s " +
           "WHERE s.like = true " +
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
    
    // 사용자가 받은 좋아요 수 (특정 조건)
    @Query("SELECT COUNT(s) FROM Swipe s WHERE s.toUser = :toUser AND s.like = :like")
    int countByToUserAndLike(@Param("toUser") User toUser, @Param("like") boolean like);
} 