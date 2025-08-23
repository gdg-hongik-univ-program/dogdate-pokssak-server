package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.ChatMessageDto;
import com.example.dogmeeting.dto.ChatMessageResponse;
import com.example.dogmeeting.dto.ChatRoomResponse;
import com.example.dogmeeting.dto.MessageType;
import com.example.dogmeeting.entity.*;
import com.example.dogmeeting.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessageSendingOperations messagingTemplate;

    @InjectMocks
    private ChatServiceImpl chatService;

    private User user1, user2;
    private Match match;
    private ChatRoom chatRoom;
    private ChatMessage chatMessage;

    @BeforeEach
    void setUp() {
        // 테스트용 데이터 준비
        user1 = User.builder()
                .id(1L)
                .userId("user1")
                .nickname("사용자1")
                .password("password")
                .gender("MALE")
                .city("서울특별시")
                .district("강남구")
                .createdAt(LocalDateTime.now())
                .build();

        user2 = User.builder()
                .id(2L)
                .userId("user2")
                .nickname("사용자2")
                .password("password")
                .gender("FEMALE")
                .city("서울특별시")
                .district("강남구")
                .createdAt(LocalDateTime.now())
                .build();

        match = Match.builder()
                .id(1L)
                .user1(user1)
                .user2(user2)
                .status("ACTIVE")
                .build();

        chatRoom = ChatRoom.builder()
                .id(1L)
                .match(match)
                .createdAt(LocalDateTime.now())
                .build();

        chatMessage = ChatMessage.builder()
                .id(1L)
                .chatRoom(chatRoom)
                .sender(user1)
                .content("안녕하세요!")
                .read(false)
                .build();
    }

    @Test
    @DisplayName("매치 기반 채팅방 생성 테스트")
    void createChatRoom_Success() {
        // Given
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(chatRoomRepository.findByMatchId(1L)).thenReturn(null);
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);

        // When
        ChatRoomResponse result = chatService.createChatRoom(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getMatchId()).isEqualTo(match.getId());
        verify(chatRoomRepository).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("이미 존재하는 채팅방 조회 테스트")
    void createChatRoom_AlreadyExists() {
        // Given
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(chatRoomRepository.findByMatchId(1L)).thenReturn(chatRoom);

        // When
        ChatRoomResponse result = chatService.createChatRoom(1L);

        // Then
        assertThat(result.getId()).isEqualTo(chatRoom.getId());
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("채팅 기록 조회 테스트")
    void getChatHistory_Success() {
        // Given
        List<ChatMessage> messages = Arrays.asList(chatMessage);
        when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(1L)).thenReturn(messages);

        // When
        List<ChatMessageResponse> result = chatService.getChatHistory(1L, 1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("안녕하세요!");
        assertThat(result.get(0).getSenderNickname()).isEqualTo("사용자1");
    }

    @Test
    @DisplayName("메시지 전송 테스트")
    void sendMessage_Success() {
        // Given
        ChatMessageDto messageDto = ChatMessageDto.builder()
                .chatroomId(1L)
                .senderId(1L)
                .content("테스트 메시지")
                .type(MessageType.CHAT)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(chatMessage);

        // When
        chatService.sendMessage(messageDto);

        // Then
        verify(chatMessageRepository).save(any(ChatMessage.class));
        verify(messagingTemplate).convertAndSend(
                eq("/sub/chat/room/1"),
                any(ChatMessageDto.class)
        );
    }

    @Test
    @DisplayName("읽지 않은 메시지 수 조회 테스트")
    void getUnreadMessageCount_Success() {
        // Given
        when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.countByChatRoomIdAndSenderIdNotAndReadFalse(1L, 2L)).thenReturn(3);

        // When
        int result = chatService.getUnreadMessageCount(1L, 2L);

        // Then
        assertThat(result).isEqualTo(3);
    }

    @Test
    @DisplayName("메시지 읽음 처리 테스트")
    void markMessagesAsRead_Success() {
        // Given
        List<ChatMessage> unreadMessages = Arrays.asList(chatMessage);
        when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(chatRoom));
        when(chatMessageRepository.findByChatRoomIdAndSenderIdNotAndReadFalse(1L, 2L))
                .thenReturn(unreadMessages);

        // When
        chatService.markMessagesAsRead(1L, 2L);

        // Then
        verify(chatMessageRepository).saveAll(unreadMessages);
    }

    @Test
    @DisplayName("권한 없는 채팅방 접근 테스트")
    void validateChatRoomAccess_Unauthorized() {
        // Given
        User unauthorizedUser = User.builder()
                .id(3L)
                .userId("user3")
                .nickname("사용자3")
                .build();

        when(chatRoomRepository.findById(1L)).thenReturn(Optional.of(chatRoom));

        // When & Then
        assertThatThrownBy(() -> chatService.getChatHistory(1L, 3L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 채팅방에 접근할 권한이 없습니다.");
    }
}