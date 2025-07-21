package com.example.dogmeeting.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
public class S3Config {

    @Value("${aws.accessKeyId}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${aws.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        log.info("S3Config 초기화 중...");
        log.info("AWS Region: {}", region);
        log.info("AWS Access Key 존재 여부: {}", accessKey != null && !accessKey.isEmpty());
        log.info("AWS Secret Key 존재 여부: {}", secretKey != null && !secretKey.isEmpty());
        
        if (accessKey == null || accessKey.isEmpty() || secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException("AWS 자격증명이 설정되지 않았습니다. application.properties를 확인하세요.");
        }
        
        try {
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
            
            S3Client s3Client = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                    .build();
                    
            log.info("S3Client 생성 완료");
            return s3Client;
        } catch (Exception e) {
            log.error("S3Client 생성 실패: {}", e.getMessage(), e);
            throw e;
        }
    }
} 