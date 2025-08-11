// src/main/java/com/example/dogmeeting/config/WebConfig.java
package com.example.dogmeeting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
                .allowedOrigins("*") // 모든 출처를 허용 (개발 초기 단계에서만 사용)
                //.allowedOrigins("http://your-frontend-domain.com", "http://localhost:3000") // 실제 운영 시에는 프론트엔드 주소만 명시
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true) // 쿠키/세션 정보 허용
                .maxAge(3600); // pre-flight 요청의 캐시 시간(초)
    }
}
