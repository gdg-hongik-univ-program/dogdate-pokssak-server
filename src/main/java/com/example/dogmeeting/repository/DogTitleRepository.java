package com.example.dogmeeting.repository;

import com.example.dogmeeting.entity.Dog;
import com.example.dogmeeting.entity.DogTitle;
import com.example.dogmeeting.entity.Title;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DogTitleRepository extends JpaRepository<DogTitle, Long> {
    
    List<DogTitle> findByDog(Dog dog);
    
    List<DogTitle> findByTitle(Title title);
    
    Optional<DogTitle> findByDogAndTitle(Dog dog, Title title);
    
    List<DogTitle> findByDogId(Long dogId);
} 