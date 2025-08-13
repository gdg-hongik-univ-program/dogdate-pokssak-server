package com.example.dogmeeting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // API 서버에서는 일반적으로 비활성화
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) // WebSocket을 위한 프레임 옵션 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/users/**",  "/api/regions/**", "/api/swipes/**", "/api/matches/**").permitAll() // 유저, 지역, 스와이프, 매치 조회 경로는 허용
                        .requestMatchers("/api/dogs/**").permitAll() // 임시로 강아지 관련 모든 API 허용 (S3 테스트용)
                        .requestMatchers("/api/chat/**").permitAll() // 채팅 REST API 허용
                        .requestMatchers("/h2-console/**").permitAll() // H2 콘솔 접근 허용
                        .requestMatchers("/ws-stomp/**").permitAll() // WebSocket 엔드포인트 허용
                        .requestMatchers("/ws-stomp").permitAll() // WebSocket 엔드포인트 허용
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증된 사용자만 접근 허용
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}