package com.example.dogmeeting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "dogs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dog_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String breed;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String gender;

    @Column(length = 500)
    private String description;

    @Column(name = "photo_url")
    private String photoUrl;

    @OneToMany(mappedBy = "dog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DogTitle> dogTitles;

    public void updateInfo(String name, String breed, Integer age, String gender, String description) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.description = description;
    }

    public void updatePhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
} 