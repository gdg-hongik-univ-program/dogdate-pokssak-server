package com.example.dogmeeting.repository;

import com.example.dogmeeting.entity.Title;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TitleRepository extends JpaRepository<Title, Long> {
    
    Optional<Title> findByName(String name);
} 