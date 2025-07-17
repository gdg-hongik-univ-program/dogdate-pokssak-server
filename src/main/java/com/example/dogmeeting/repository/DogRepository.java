package com.example.dogmeeting.repository;

import com.example.dogmeeting.entity.Dog;
import com.example.dogmeeting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {
    
    List<Dog> findByUser(User user);
    
    List<Dog> findByUserId(Long userId);
    
    List<Dog> findByBreed(String breed);
    
    List<Dog> findByAgeBetween(Integer minAge, Integer maxAge);
    
    @Query("SELECT d FROM Dog d WHERE d.user.city = :city")
    List<Dog> findByUserCity(@Param("city") String city);
    
    @Query("SELECT d FROM Dog d WHERE d.user.city = :city AND d.user.district = :district")
    List<Dog> findByUserCityAndDistrict(@Param("city") String city, @Param("district") String district);
} 