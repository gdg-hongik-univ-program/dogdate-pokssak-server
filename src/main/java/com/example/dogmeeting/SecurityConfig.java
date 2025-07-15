package com.example.dogmeeting;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // API 서버에서는 일반적으로 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/users/signup", "/auth/users/login").permitAll() // 회원가입, 로그인 경로는 허용
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증된 사용자만 접근 허용
                );
        return http.build();
    }
}