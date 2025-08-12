# DogMeeting API 문서

## 개요
DogMeeting은 강아지와 주인을 위한 소셜 매칭 플랫폼입니다. 이 문서는 모든 REST API 엔드포인트를 정리합니다.

**Base URL**: `http://localhost:8080`

---

## 📱 사용자 관리 API (`/api/users`)

### 회원가입
```http
POST /api/users/signup
Content-Type: application/json

{
  "userId": "testuser",
  "nickname": "테스트유저",
  "password": "password123",
  "confirmPassword": "password123",
  "gender": "MALE",
  "city": "서울특별시",
  "district": "강남구"
}
```

**응답**
```http
HTTP/1.1 201 Created
Content-Type: text/plain

회원가입이 성공적으로 완료되었습니다.
```

### 로그인
```http
POST /api/users/login
Content-Type: application/json

{
  "userId": "testuser",
  "password": "password123"
}
```

**응답**
```http
HTTP/1.1 200 OK
Content-Type: text/plain

로그인 성공! 환영합니다, 테스트유저님!
```

### 사용자 조회 (ID)
```http
GET /api/users/{userId}
```

**응답**
```json
{
  "id": 1,
  "userId": "testuser",
  "nickname": "테스트유저",
  "gender": "MALE",
  "city": "서울특별시",
  "district": "강남구",
  "createdAt": "2025-01-28T10:00:00"
}
```

### 사용자 조회 (닉네임)
```http
GET /api/users/nickname/{nickname}
```

### 매칭 대상 조회
```http
GET /api/users/{userId}/potential-matches
```

**응답**: 같은 지역의 반대 성별 사용자 목록

### 프로필 수정
```http
PUT /api/users/{userId}/profile
Content-Type: application/x-www-form-urlencoded

nickname=새닉네임&gender=FEMALE&city=부산광역시&district=해운대구
```

---

## 🐕 강아지 관리 API (`/api/dogs`)

### 강아지 등록
```http
POST /api/dogs/users/{loginId}
Content-Type: multipart/form-data

dogInfo={
  "name": "멍멍이",
  "breed": "골든리트리버", 
  "age": 3,
  "gender": "male",
  "description": "착하고 순한 강아지입니다.",
  "photoUrl": null
}
image=[이미지 파일] (선택사항)
```

**주요 변경사항**: 
- URL 파라미터가 `{userId}` (숫자 ID)에서 `{loginId}` (로그인 아이디 문자열)로 변경
- `dogInfo`에 `gender` 필드 추가 (필수)

**응답**
```http
HTTP/1.1 201 Created
Content-Type: text/plain

강아지 정보가 성공적으로 등록되었습니다. ID: 1
```

### 강아지 조회
```http
GET /api/dogs/{dogId}
```

**응답**
```json
{
  "id": 1,
  "name": "멍멍이",
  "breed": "골든리트리버",
  "age": 3,
  "gender": "male",
  "description": "착하고 순한 강아지입니다.",
  "photoUrl": null,
  "userId": 1
}
```

### 강아지 상세 프로필 조회
```http
GET /api/dogs/{dogId}/profile
```

**응답**
```json
{
  "dogId": 1,
  "name": "멍멍이",
  "breed": "골든리트리버",
  "age": 3,
  "description": "착하고 순한 강아지입니다.",
  "photoUrl": null,
  "ownerId": 1,
  "ownerNickname": "테스트유저",
  "ownerGender": "MALE",
  "ownerCity": "서울특별시",
  "ownerDistrict": "강남구",
  "likeCount": 5,
  "rank": 1,
  "titles": []
}
```

### 사용자의 강아지 목록 조회
```http
GET /api/dogs/users/{userId}
```

### 강아지 정보 수정
```http
PUT /api/dogs/{dogId}
Content-Type: multipart/form-data

dogInfo={
  "name": "새이름",
  "breed": "골든리트리버",
  "age": 4,
  "gender": "female",
  "description": "업데이트된 설명",
  "photoUrl": null
}
image=[새 이미지 파일] (선택사항)
```

**주요 변경사항**: 
- Content-Type이 `application/json`에서 `multipart/form-data`로 변경
- 이미지 파일 업로드 지원
- `gender` 필드 추가

### 강아지 삭제
```http
DELETE /api/dogs/{dogId}
```

### 강아지 이미지 업로드/변경
```http
POST /api/dogs/{dogId}/image
Content-Type: multipart/form-data

userId={userId}&image=[이미지 파일]
```

**응답**
```http
HTTP/1.1 200 OK
Content-Type: text/plain

강아지 사진이 성공적으로 업로드되었습니다. URL: [S3_URL]
```

### 강아지 이미지 삭제
```http
DELETE /api/dogs/{dogId}/image
```

**응답**
```http
HTTP/1.1 200 OK
Content-Type: text/plain

강아지 사진이 성공적으로 삭제되었습니다.
```

---

## 💝 스와이프/좋아요 API (`/api/swipes`)

### 스와이프 실행
```http
POST /api/swipes/users/{fromUserId}
Content-Type: application/json

{
  "toUserId": 2
}
```

**응답 (매칭 성공시)**
```json
{
  "id": 1,
  "user1Id": 1,
  "user2Id": 2,
  "user1Nickname": "사용자1",
  "user2Nickname": "사용자2",
  "status": "ACTIVE",
  "createdAt": "2025-01-28T10:00:00"
}
```

**응답 (매칭 실패시)**
```http
HTTP/1.1 200 OK
Content-Type: text/plain

스와이프가 완료되었습니다.
```

### 좋아요 토글
```http
POST /api/swipes/like/{fromUserId}/{toUserId}
```

**응답**
```http
HTTP/1.1 200 OK
Content-Type: text/plain

좋아요를 눌렀습니다.
```

### 좋아요 상태 확인
```http
GET /api/swipes/like-status/{fromUserId}/{toUserId}
```

**응답**
```json
true
```

### 스와이프 여부 확인
```http
GET /api/swipes/users/{fromUserId}/check/{toUserId}
```

**응답**
```json
false
```

---

## 💕 매칭 관리 API (`/api/matches`)

### 사용자 매칭 목록 조회
```http
GET /api/matches/users/{userId}
```

**응답**
```json
[
  {
    "id": 1,
    "user1Id": 1,
    "user2Id": 2,
    "user1Nickname": "사용자1",
    "user2Nickname": "사용자2",
    "status": "ACTIVE",
    "createdAt": "2025-01-28T10:00:00"
  }
]
```

### 활성 매칭 목록 조회
```http
GET /api/matches/users/{userId}/active
```

### 매칭 상태 변경
```http
PUT /api/matches/{matchId}/status?status=INACTIVE
```

---

## 🏠 홈 화면 API (`/api/home`)

### 사용자 프로필 조회 (홈용)
```http
GET /api/home/profile/{userId}
```

**응답**
```json
{
  "id": 1,
  "userId": "testuser",
  "nickname": "테스트유저",
  "gender": "MALE",
  "city": "서울특별시",
  "district": "강남구",
  "createdAt": "2025-01-28T10:00:00",
  "dogs": [
    {
      "id": 1,
      "name": "멍멍이",
      "breed": "골든리트리버",
      "age": 3,
      "description": "착한 강아지",
      "photoUrl": null,
      "userId": 1
    }
  ],
  "matchCount": 3,
  "rankingScore": 15
}
```

### 사용자 프로필 수정 (홈용)
```http
PUT /api/home/profile/{userId}
Content-Type: application/json

{
  "nickname": "새닉네임",
  "gender": "MALE",
  "city": "서울특별시",
  "district": "강남구"
}
```

### 사용자 랭킹 조회
```http
GET /api/home/ranking?page=0&size=10
```

**응답**
```json
[
  {
    "id": 1,
    "nickname": "테스트유저",
    "city": "서울특별시",
    "district": "강남구",
    "matchCount": 3,
    "rankingScore": 15,
    "rank": 1,
    "mainDogPhotoUrl": null,
    "mainDogName": "멍멍이"
  }
]
```

### 지역별 강아지 TOP 3
```http
GET /api/home/regional-dogs/{city}?limit=3
```

**응답**
```json
[
  {
    "dogId": 1,
    "dogName": "멍멍이",
    "breed": "골든리트리버",
    "age": 3,
    "photoUrl": null,
    "description": "착한 강아지",
    "ownerId": 1,
    "ownerNickname": "테스트유저",
    "ownerCity": "서울특별시",
    "ownerDistrict": "강남구",
    "likeCount": 15,
    "rank": 1
  }
]
```

### 전체 강아지 랭킹
```http
GET /api/home/dog-ranking?page=0&size=10
```

### 특정 사용자 상세 정보
```http
GET /api/home/user/{userId}
```

---

## 💬 실시간 채팅 API (WebSocket STOMP)

### WebSocket 연결
```javascript
// 연결 설정
const socket = new SockJS('http://localhost:8080/ws-stomp');
const stompClient = Stomp.over(socket);

// 연결
stompClient.connect({}, function(frame) {
    console.log('Connected: ' + frame);
});
```

### 채팅방 구독
```javascript
// 특정 채팅방 구독
stompClient.subscribe('/sub/chat/room/{chatroomId}', function(message) {
    const chatMessage = JSON.parse(message.body);
    // 메시지 처리 로직
});
```

### 메시지 전송
```javascript
// 일반 채팅 메시지 전송
stompClient.send('/pub/chat/message', {}, JSON.stringify({
    chatroomId: 1,
    senderId: 123,
    content: '안녕하세요!',
    type: 'CHAT'
}));

// 채팅방 입장 알림
stompClient.send('/pub/chat/enter', {}, JSON.stringify({
    chatroomId: 1,
    senderId: 123,
    type: 'ENTER'
}));

// 채팅방 퇴장 알림
stompClient.send('/pub/chat/leave', {}, JSON.stringify({
    chatroomId: 1,
    senderId: 123,
    type: 'LEAVE'
}));
```

### 메시지 형식

**전송 메시지 (Client → Server)**
```json
{
  "chatroomId": 1,
  "senderId": 123,
  "content": "안녕하세요!",
  "type": "CHAT"
}
```

**수신 메시지 (Server → Client)**
```json
{
  "chatroomId": 1,
  "senderId": 123,
  "content": "안녕하세요!",
  "type": "CHAT",
  "senderNickname": "테스트유저",
  "timestamp": "2025-08-01T10:30:00",
  "isRead": false
}
```

### 메시지 타입
- `CHAT`: 일반 채팅 메시지
- `ENTER`: 채팅방 입장 알림
- `LEAVE`: 채팅방 퇴장 알림

### WebSocket 엔드포인트
- **연결 엔드포인트**: `/ws-stomp`
- **구독 경로**: `/sub/chat/room/{chatroomId}`
- **발행 경로**: 
  - `/pub/chat/message` - 일반 메시지
  - `/pub/chat/enter` - 입장 알림
  - `/pub/chat/leave` - 퇴장 알림

### 채팅 REST API

#### 채팅 기록 조회
```http
GET /api/chat/{chatroomId}/history?userId={userId}
```

**응답**
```json
[
  {
    "id": 1,
    "chatroomId": 1,
    "senderId": 123,
    "senderNickname": "테스트유저",
    "content": "안녕하세요!",
    "sentAt": "2025-08-01T10:30:00",
    "read": false
  }
]
```

#### 읽지 않은 메시지 수 조회
```http
GET /api/chat/{chatroomId}/unread-count?userId={userId}
```

**응답**
```json
{
  "unreadCount": 5
}
```

#### 메시지 읽음 처리
```http
PUT /api/chat/{chatroomId}/read?userId={userId}
```

**응답**
```http
HTTP/1.1 200 OK
Content-Type: text/plain

메시지를 읽음 처리했습니다.
```

#### 채팅방 생성 (매치 기반)
```http
POST /api/chat/room/match/{matchId}
```

**응답**
```json
{
  "id": 1,
  "matchId": 1,
  "createdAt": "2025-08-01T10:00:00"
}
```

#### 매치 기반 채팅방 조회
```http
GET /api/chat/room/match/{matchId}
```

**응답**
```json
{
  "id": 1,
  "matchId": 1,
  "createdAt": "2025-08-01T10:00:00"
}
```

### 채팅 사용 시나리오

1. **매치 성공 후 채팅방 생성**
   ```http
   POST /api/chat/room/match/1
   ```

2. **WebSocket 연결 및 채팅방 구독**
   ```javascript
   stompClient.subscribe('/sub/chat/room/1', handleMessage);
   ```

3. **실시간 메시지 전송**
   ```javascript
   stompClient.send('/pub/chat/message', {}, JSON.stringify(message));
   ```

4. **채팅 기록 조회**
   ```http
   GET /api/chat/1/history?userId=123
   ```

5. **읽음 처리**
   ```http
   PUT /api/chat/1/read?userId=123
   ```

---

## 🌍 지역 관리 API (`/api/regions`)

### 모든 시/도 목록
```http
GET /api/regions/cities
```

**응답**
```json
[
  "서울특별시",
  "부산광역시",
  "대구광역시",
  "인천광역시"
]
```

### 특정 시/도의 구/군 목록
```http
GET /api/regions/cities/{cityName}/districts
```

**응답**
```json
[
  "강남구",
  "강동구",
  "강북구",
  "강서구"
]
```

### 모든 지역 데이터
```http
GET /api/regions/all
```

**응답**
```json
[
  {
    "city": "서울특별시",
    "districts": [
      "강남구",
      "강동구",
      "강북구"
    ]
  }
]
```

---

## 📋 공통 응답 형식

### 성공 응답
- **200 OK**: 조회 성공
- **201 Created**: 생성 성공

### 오류 응답
- **400 Bad Request**: 잘못된 요청
- **404 Not Found**: 리소스를 찾을 수 없음
- **500 Internal Server Error**: 서버 오류

### 오류 응답 예시
```json
{
  "timestamp": "2025-01-28T10:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "사용자를 찾을 수 없습니다.",
  "path": "/api/users/999"
}
```

---

## 🔧 주요 데이터 타입

### Gender
- `MALE`: 남성
- `FEMALE`: 여성

### Match Status
- `ACTIVE`: 활성 매칭
- `INACTIVE`: 비활성 매칭

### 페이징 파라미터
- `page`: 페이지 번호 (0부터 시작, 기본값: 0)
- `size`: 페이지 크기 (기본값: 10)

---

## 💡 사용 팁

1. **인증**: 현재 버전에서는 별도 인증 토큰이 필요하지 않습니다.
2. **이미지 업로드**: S3 연동 기능은 현재 비활성화되어 있습니다.
3. **페이징**: 랭킹 API들은 페이징을 지원합니다.
4. **지역 필터링**: 매칭은 같은 지역(시/구) 내에서만 이루어집니다.
5. **좋아요 시스템**: 스와이프와 독립적으로 좋아요 기능을 사용할 수 있습니다.

---

## 🚀 최근 주요 변경사항 (v1.1)

### 강아지 등록 API 변경
- **URL 변경**: `POST /api/dogs/users/{userId}` → `POST /api/dogs/users/{loginId}`
- **파라미터 타입 변경**: 숫자 ID → 로그인 아이디 (문자열)
- **새 필드 추가**: `dogInfo`에 `gender` 필드 필수 추가

### 프론트엔드 수정 필요사항
```javascript
// 이전 버전
fetch(`/api/dogs/users/${userInfo.id}`, { ... });

// 새 버전  
fetch(`/api/dogs/users/${userInfo.userId}`, { ... });

// dogInfo에 gender 필드 추가 필요
const dogInfo = {
  name: "멍멍이",
  breed: "골든리트리버",
  age: 3,
  gender: "male",  // 새로 추가된 필수 필드
  description: "설명"
};
```

### 매핑 변경 이유
- 프론트엔드에서 보내는 `userInfo.userId`는 로그인 아이디("testuser1")
- 백엔드에서 숫자 PK(1, 2, 3...)가 아닌 문자열 로그인 아이디로 사용자 식별
- 보안상 내부 PK 노출 방지 및 RESTful API 설계 개선

---

*이 문서는 DogMeeting API v1.1을 기준으로 작성되었습니다.*