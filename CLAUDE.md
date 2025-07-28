# DogMeeting - 강아지 소셜 매칭 앱

## 프로젝트 개요
DogMeeting은 강아지와 주인을 위한 소셜 매칭 플랫폼입니다. Tinder와 유사한 스와이프 기능을 통해 반려견끼리 매칭되고, 주인들이 만날 수 있는 서비스입니다.

## 기술 스택
- **Framework**: Spring Boot 3.5.3
- **Language**: Java 21
- **Database**: H2 Database (개발용), JPA/Hibernate
- **Security**: Spring Security
- **Cloud Storage**: AWS S3 (사진 업로드)
- **Build Tool**: Gradle
- **Additional**: Lombok, Validation

## 주요 기능

### 1. 사용자 관리
- 회원가입/로그인 시스템
- 프로필 관리 (닉네임, 성별, 지역)
- 지역 기반 필터링 (시/구 단위)

### 2. 강아지 프로필
- 강아지 정보 등록 (이름, 품종, 나이, 설명)
- 사진 업로드 (S3 연동)
- 칭호/뱃지 시스템

### 3. 매칭 시스템
- 스와이프 기능 (좋아요/패스)
- 상호 좋아요 시 매치 생성
- 매치 히스토리 관리

### 4. 소셜 기능
- 채팅 시스템 (매치된 사용자간)
- 리뷰 시스템
- 랭킹 시스템

## 프로젝트 구조

```
src/main/java/com/example/dogmeeting/
├── DogmeetingApplication.java          # 메인 애플리케이션
├── SecurityConfig.java                 # 보안 설정
├── config/
│   └── S3Config.java                   # AWS S3 설정
├── controller/                         # REST API 컨트롤러
│   ├── UserController.java             # 사용자 관리
│   ├── DogController.java              # 강아지 프로필
│   ├── SwipeController.java            # 스와이프/매칭
│   ├── MatchController.java            # 매치 관리
│   ├── HomeController.java             # 홈 화면
│   └── RegionController.java           # 지역 관리
├── dto/                                # 데이터 전송 객체
├── entity/                             # JPA 엔티티
│   ├── User.java                       # 사용자
│   ├── Dog.java                        # 강아지
│   ├── Swipe.java                      # 스와이프 기록
│   ├── Match.java                      # 매치
│   ├── ChatRoom.java & ChatMessage.java # 채팅
│   └── Review.java                     # 리뷰
├── repository/                         # 데이터 액세스 계층
├── service/                           # 비즈니스 로직
└── exception/                         # 예외 처리
```

## 개발 명령어

### 애플리케이션 실행
```bash
./gradlew bootRun
```

### 테스트 실행
```bash
./gradlew test
```

### 빌드
```bash
./gradlew build
```

### 정적 분석 (권장)
```bash
./gradlew check
```

## API 엔드포인트

### 사용자 관리
- `POST /api/users/join` - 회원가입
- `POST /api/users/login` - 로그인
- `GET /api/users/{id}` - 사용자 정보 조회
- `PUT /api/users/{id}` - 프로필 수정

### 강아지 관리
- `POST /api/dogs` - 강아지 등록
- `GET /api/dogs/owner/{userId}` - 특정 사용자의 강아지 목록
- `PUT /api/dogs/{id}` - 강아지 정보 수정
- `POST /api/dogs/{id}/photo` - 사진 업로드

### 매칭 시스템
- `POST /api/swipes/users/{fromUserId}` - 스와이프
- `POST /api/swipes/like/{fromUserId}/{toUserId}` - 좋아요 토글
- `GET /api/swipes/like-status/{fromUserId}/{toUserId}` - 좋아요 상태 확인

### 홈/지역
- `GET /api/home/{userId}` - 홈 화면 데이터
- `GET /api/regions` - 지역 목록

## 데이터베이스 설계

### 주요 엔티티 관계
- User (1) ↔ (N) Dog - 사용자와 강아지
- User (1) ↔ (N) Swipe - 사용자와 스와이프 기록
- Match (N) ↔ (N) User - 매치된 사용자들
- ChatRoom (1) ↔ (N) ChatMessage - 채팅방과 메시지

## 보안
- Spring Security 기반 인증/인가
- 비밀번호 암호화 (BCrypt)
- 세션 기반 인증

## 파일 업로드
- AWS S3 연동으로 강아지 사진 저장
- 파일 업로드 서비스 구현됨

## 개발 시 주의사항

1. **엔티티 수정 시**: 양방향 연관관계 주의
2. **API 응답**: DTO 사용으로 엔티티 직접 노출 방지
3. **파일 업로드**: S3 설정 확인 필요
4. **지역 코드**: 시/구 단위 지역 데이터 관리
5. **매칭 로직**: 중복 스와이프 방지 로직 구현됨

## 환경 설정
- `application.properties`에서 데이터베이스 및 S3 설정
- AWS 자격증명 설정 필요

## Git 브랜치 전략
- main: `master` 브랜치
- current: `version1.0` 브랜치에서 개발 중

## 향후 개선 사항
- 실시간 채팅 기능 (WebSocket)
- 푸시 알림
- 지도 기반 위치 서비스
- 강아지 산책 약속 기능