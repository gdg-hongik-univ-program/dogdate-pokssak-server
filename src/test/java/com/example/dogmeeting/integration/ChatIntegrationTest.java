package com.example.dogmeeting.integration;

import com.example.dogmeeting.dto.ChatMessageDto;
import com.example.dogmeeting.dto.ChatRoomResponse;
import com.example.dogmeeting.dto.MessageType;
import com.example.dogmeeting.entity.*;
import com.example.dogmeeting.repository.*;
import com.example.dogmeeting.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class ChatIntegrationTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    private User user1, user2;
    private Match match;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        user1 = User.builder()
                .userId("testUser1")
                .nickname("테스트유저1")
                .password("password")
                .gender("MALE")
                .city("서울특별시")
                .district("강남구")
                .createdAt(LocalDateTime.now())
                .build();

        user2 = User.builder()
                .userId("testUser2")
                .nickname("테스트유저2")
                .password("password")
                .gender("FEMALE")
                .city("서울특별시")
                .district("강남구")
                .createdAt(LocalDateTime.now())
                .build();

        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        match = Match.builder()
                .user1(user1)
                .user2(user2)
                .status("ACTIVE")
                .build();

        match = matchRepository.save(match);
    }

    @Test
    @DisplayName("채팅방 생성 및 조회 통합 테스트")
    void createAndFindChatRoom() {
        // When - 채팅방 생성
        ChatRoomResponse chatRoom = chatService.createChatRoom(match.getId());

        // Then - 채팅방이 정상적으로 생성됨
        assertThat(chatRoom).isNotNull();
        assertThat(chatRoom.getId()).isNotNull();
        assertThat(chatRoom.getMatchId()).isEqualTo(match.getId());

        // When - 매치 기반 채팅방 조회
        ChatRoomResponse foundChatRoom = chatService.findChatRoomByMatchId(match.getId());

        // Then - 동일한 채팅방이 조회됨
        assertThat(foundChatRoom).isNotNull();
        assertThat(foundChatRoom.getId()).isEqualTo(chatRoom.getId());
    }

    @Test
    @DisplayName("메시지 전송 및 채팅 기록 조회 통합 테스트")
    void sendMessageAndGetHistory() {
        // Given - 채팅방 생성
        ChatRoomResponse chatRoom = chatService.createChatRoom(match.getId());

        // When - 메시지 전송
        ChatMessageDto messageDto = ChatMessageDto.builder()
                .chatroomId(chatRoom.getId())
                .senderId(user1.getId())
                .content("안녕하세요! 테스트 메시지입니다.")
                .type(MessageType.CHAT)
                .build();

        chatService.sendMessage(messageDto);

        // Then - 채팅 기록 조회
        var chatHistory = chatService.getChatHistory(chatRoom.getId(), user1.getId());

        assertThat(chatHistory).hasSize(1);
        assertThat(chatHistory.get(0).getContent()).isEqualTo("안녕하세요! 테스트 메시지입니다.");
        assertThat(chatHistory.get(0).getSenderNickname()).isEqualTo("테스트유저1");
        assertThat(chatHistory.get(0).getRead()).isFalse();
    }

    @Test
    @DisplayName("읽지 않은 메시지 수 조회 및 읽음 처리 통합 테스트")
    void unreadCountAndMarkAsRead() {
        // Given - 채팅방 생성 및 메시지 전송
        ChatRoomResponse chatRoom = chatService.createChatRoom(match.getId());

        ChatMessageDto messageDto1 = ChatMessageDto.builder()
                .chatroomId(chatRoom.getId())
                .senderId(user1.getId())
                .content("첫 번째 메시지")
                .type(MessageType.CHAT)
                .build();

        ChatMessageDto messageDto2 = ChatMessageDto.builder()
                .chatroomId(chatRoom.getId())
                .senderId(user1.getId())
                .content("두 번째 메시지")
                .type(MessageType.CHAT)
                .build();

        chatService.sendMessage(messageDto1);
        chatService.sendMessage(messageDto2);

        // When - user2가 읽지 않은 메시지 수 조회
        int unreadCount = chatService.getUnreadMessageCount(chatRoom.getId(), user2.getId());

        // Then - 2개의 읽지 않은 메시지가 있음
        assertThat(unreadCount).isEqualTo(2);

        // When - user2가 메시지를 읽음 처리
        chatService.markMessagesAsRead(chatRoom.getId(), user2.getId());

        // Then - 읽지 않은 메시지가 0개가 됨
        int unreadCountAfterRead = chatService.getUnreadMessageCount(chatRoom.getId(), user2.getId());
        assertThat(unreadCountAfterRead).isEqualTo(0);
    }

    @Test
    @DisplayName("권한 없는 사용자의 채팅방 접근 테스트")
    void unauthorizedChatRoomAccess() {
        // Given - 채팅방 생성
        ChatRoomResponse chatRoom = chatService.createChatRoom(match.getId());

        // 권한 없는 사용자 생성
        User unauthorizedUser = User.builder()
                .userId("unauthorized")
                .nickname("권한없는유저")
                .password("password")
                .gender("MALE")
                .city("부산광역시")
                .district("해운대구")
                .createdAt(LocalDateTime.now())
                .build();
        unauthorizedUser = userRepository.save(unauthorizedUser);

        // When & Then - 권한 없는 사용자가 채팅 기록 조회 시 예외 발생
        Long unauthorizedUserId = unauthorizedUser.getId();
        assertThatThrownBy(() -> chatService.getChatHistory(chatRoom.getId(), unauthorizedUserId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 채팅방에 접근할 권한이 없습니다.");
    }

    @Test
    @DisplayName("중복 채팅방 생성 방지 테스트")
    void preventDuplicateChatRoomCreation() {
        // When - 동일한 매치로 채팅방을 두 번 생성
        ChatRoomResponse chatRoom1 = chatService.createChatRoom(match.getId());
        ChatRoomResponse chatRoom2 = chatService.createChatRoom(match.getId());

        // Then - 동일한 채팅방이 반환됨
        assertThat(chatRoom1.getId()).isEqualTo(chatRoom2.getId());

        // DB에는 하나의 채팅방만 존재
        long chatRoomCount = chatRoomRepository.count();
        assertThat(chatRoomCount).isEqualTo(1);
    }
}