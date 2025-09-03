# 🐕 DogMeeting

> 강아지와 주인을 위한 소셜 매칭 플랫폼

DogMeeting은 반려견을 키우는 사람들을 위한 특별한 소셜 매칭 플랫폼입니다. Tinder와 유사한 직관적인 스와이프 기능을 통해 우리 동네의 다른 반려견들과 만남을 주선하고, 반려견 주인들끼리 자연스럽게 소통할 수 있는 환경을 제공합니다. 

단순한 매칭을 넘어서 실시간 채팅, 지역 기반 필터링, 반려견 프로필 관리 등 다양한 기능을 통해 반려견과 주인 모두가 행복한 만남을 가질 수 있도록 돕습니다. 우리 강아지에게 새로운 친구를, 나에게는 같은 관심사를 가진 이웃을 찾아보세요!

## ✨ 주요 기능

### 🔐 사용자 관리
- 회원가입/로그인 시스템
- 프로필 관리 (닉네임, 성별, 지역)
- 지역 기반 필터링 (시/구 단위)

### 🐾 반려견 프로필
- 강아지 정보 등록 (이름, 품종, 나이, 설명)
- AWS S3 연동 사진 업로드
- 칭호/뱃지 시스템

### 💕 매칭 시스템
- 스와이프 기능 (좋아요/패스)
- 상호 좋아요 시 매치 생성
- 매치 히스토리 관리

### 💬 실시간 채팅
- WebSocket + STOMP 기반 실시간 채팅
- 매치된 사용자간 안전한 채팅
- 메시지 읽음 상태 관리
- 채팅 이력 보관

### 🏆 소셜 기능
- 리뷰 시스템
- 랭킹 시스템
- 지역별 인기 강아지 조회

## 🛠 기술 스택

- **Framework**: Spring Boot 3.5.3
- **Language**: Java 21
- **Database**: MySQL 8.0 (JPA/Hibernate)
- **Security**: Spring Security
- **Real-time**: WebSocket + STOMP
- **Cloud Storage**: AWS S3
- **Build Tool**: Gradle
- **Additional**: Lombok, Validation

## 📁 프로젝트 구조

```
src/main/java/com/example/dogmeeting/
├── DogmeetingApplication.java          # 메인 애플리케이션
├── SecurityConfig.java                 # 보안 설정
├── config/
│   ├── S3Config.java                   # AWS S3 설정
│   └── WebSocketConfig.java            # WebSocket 설정
├── controller/                         # REST API & WebSocket 컨트롤러
│   ├── UserController.java             # 사용자 관리
│   ├── DogController.java              # 강아지 프로필
│   ├── SwipeController.java            # 스와이프/매칭
│   ├── MatchController.java            # 매치 관리
│   ├── ChatController.java             # WebSocket 채팅
│   ├── ChatRestController.java         # REST 채팅 API
│   ├── HomeController.java             # 홈 화면
│   └── RegionController.java           # 지역 관리
├── dto/                                # 데이터 전송 객체
├── entity/                             # JPA 엔티티
│   ├── User.java                       # 사용자
│   ├── Dog.java                        # 강아지
│   ├── Swipe.java                      # 스와이프 기록
│   ├── Match.java                      # 매치
│   ├── ChatRoom.java                   # 채팅방
│   ├── ChatMessage.java                # 채팅 메시지
│   └── Review.java                     # 리뷰
├── repository/                         # 데이터 액세스 계층
├── service/                           # 비즈니스 로직
└── exception/                         # 예외 처리
```


## 🔐 보안 기능

- Spring Security 기반 인증/인가
- BCrypt 비밀번호 암호화
- 세션 기반 인증
- 채팅방 접근 권한 검증
- 매치 기반 채팅 권한 관리

## 🌐 실시간 채팅 아키텍처

```
[클라이언트] ↔ [WebSocket] ↔ [STOMP] ↔ [메시지 브로커] ↔ [구독자들]
                                ↓
                          [ChatController] ↔ [Database]
```

### WebSocket + STOMP 선택 이유
- **실시간성**: HTTP polling 대비 즉각적인 양방향 통신
- **효율성**: 메시지 브로커 패턴으로 효율적인 라우팅
- **확장성**: 구독-발행 모델로 다중 사용자 동시 처리
- **안정성**: SockJS fallback 지원으로 브라우저 호환성

## 📊 주요 특징

### 🎯 매칭 알고리즘
- 지역 기반 필터링 (같은 시/구 우선)
- 상호 좋아요 기반 매치 생성
- 중복 스와이프 방지

### 💾 클라우드 저장소
- AWS S3 연동 사진 업로드
- 안전한 파일 관리
- 최적화된 이미지 처리

### 📱 실시간 기능
- WebSocket 기반 즉시 메시지 전달
- 입장/퇴장 알림
- 읽음 상태 실시간 업데이트


## 👥 팀

GDG 홍익대학교 프로젝트 팀 '폭싹 물렸수다' 백엔드 팀에서 개발했습니다.


⭐ 이 프로젝트가 도움이 되었다면 Star를 눌러주세요!