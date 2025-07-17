package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.UserJoinRequest;
import com.example.dogmeeting.dto.UserResponse;
import com.example.dogmeeting.entity.User;
import com.example.dogmeeting.exception.DuplicateNicknameException;
import com.example.dogmeeting.exception.UserNotFoundException;
import com.example.dogmeeting.exception.PasswordMismatchException;
import com.example.dogmeeting.repository.UserRepository;
import com.example.dogmeeting.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegionService regionService;

    @Override
    @Transactional
    public Long joinUser(UserJoinRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        userRepository.findByNickname(request.getNickname())
                .ifPresent(u -> {
                    throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다.");
                });

        User newUser = User.builder()
                .nickname(request.getNickname())
                .password(request.getPassword())
                .gender(request.getGender())
                .city(request.getCity())
                .district(request.getDistrict())
                .build();
        
        newUser.encryptPassword(passwordEncoder);
        userRepository.save(newUser);
        
        return newUser.getId();
    }

    @Override
    public User loginUser(String nickname, String password) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserNotFoundException("닉네임을 찾을 수 없습니다."));

        if (!user.checkPassword(passwordEncoder, password)) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }
        
        return user;
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        return UserResponse.from(user);
    }

    @Override
    public UserResponse getUserByNickname(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        return UserResponse.from(user);
    }

    @Override
    public List<UserResponse> getPotentialMatches(Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        
        List<User> potentialMatches;
        
        // district가 있으면 같은 district 내에서, 없으면 같은 city 내에서 매칭
        if (currentUser.getDistrict() != null && !currentUser.getDistrict().trim().isEmpty()) {
            potentialMatches = userRepository.findPotentialMatchesInDistrict(
                    currentUser.getCity(), currentUser.getDistrict(), currentUser.getGender(), userId);
        } else {
            potentialMatches = userRepository.findPotentialMatches(
                    currentUser.getCity(), currentUser.getGender(), userId);
        }
        
        return potentialMatches.stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateUserProfile(Long userId, String nickname, String gender, String city, String district) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        
        user.updateProfile(nickname, gender, city, district);
    }
} 