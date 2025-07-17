package com.example.dogmeeting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "titles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Title {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "title_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 200)
    private String description;

    @OneToMany(mappedBy = "title", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DogTitle> dogTitles;

    public void updateTitle(String name, String description) {
        this.name = name;
        this.description = description;
    }
} 