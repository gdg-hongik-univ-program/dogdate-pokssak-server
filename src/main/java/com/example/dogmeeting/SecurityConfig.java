package com.example.dogmeeting;

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
                .csrf(csrf -> csrf.disable()) // API 서버에서는 일반적으로 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 적용
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/users/signup", "/api/users/login", "/api/regions/**").permitAll() // 회원가입, 로그인, 지역 조회 경로는 허용
                        .requestMatchers("/api/dogs/**").permitAll() // 임시로 강아지 관련 모든 API 허용 (S3 테스트용)
                        .requestMatchers("/h2-console/**").permitAll() // H2 콘솔 접근 허용
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증된 사용자만 접근 허용
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 허용할 오리진 설정 (개발/프로덕션 환경 고려)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",    // React 개발 서버
                "http://localhost:8080",    // 다른 포트의 프론트엔드
                "https://yourdomain.com"    // 프로덕션 도메인 (실제 도메인으로 변경 필요)
        ));
        
        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // 허용할 헤더
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 자격 증명 허용 (쿠키, 인증 헤더 등)
        configuration.setAllowCredentials(true);
        
        // preflight 요청 캐시 시간 (초)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration); // API 경로에만 적용
        
        return source;
    }
}