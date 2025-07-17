package com.example.dogmeeting.dto;

import com.example.dogmeeting.entity.Dog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DogResponse {
    
    private Long id;
    private Long userId;
    private String name;
    private String breed;
    private Integer age;
    private String description;
    private String photoUrl;
    
    public static DogResponse from(Dog dog) {
        return DogResponse.builder()
                .id(dog.getId())
                .userId(dog.getUser().getId())
                .name(dog.getName())
                .breed(dog.getBreed())
                .age(dog.getAge())
                .description(dog.getDescription())
                .photoUrl(dog.getPhotoUrl())
                .build();
    }
} 