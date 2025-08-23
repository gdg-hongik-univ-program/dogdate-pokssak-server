package com.example.dogmeeting.repository;

import com.example.dogmeeting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByNickname(String nickname);
    
    Optional<User> findByUserId(String userId);
    
    List<User> findByCity(String city);
    
    List<User> findByCityAndDistrict(String city, String district);
    
    List<User> findByGender(String gender);
    
    List<User> findByCityAndGender(String city, String gender);
    
    @Query("SELECT u FROM User u WHERE u.city = :city AND u.gender != :gender AND u.id != :excludeUserId")
    List<User> findPotentialMatches(@Param("city") String city, 
                                   @Param("gender") String gender, 
                                   @Param("excludeUserId") Long excludeUserId);
    
    @Query("SELECT u FROM User u WHERE u.city = :city AND u.district = :district AND u.gender != :gender AND u.id != :excludeUserId")
    List<User> findPotentialMatchesInDistrict(@Param("city") String city,
                                             @Param("district") String district,
                                             @Param("gender") String gender, 
                                             @Param("excludeUserId") Long excludeUserId);
} 