package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.ChatMessageDto;
import com.example.dogmeeting.dto.ChatMessageResponse;
import com.example.dogmeeting.dto.ChatRoomResponse;
import com.example.dogmeeting.entity.ChatMessage;
import com.example.dogmeeting.entity.ChatRoom;
import com.example.dogmeeting.entity.Match;
import com.example.dogmeeting.entity.User;
import com.example.dogmeeting.exception.UserNotFoundException;
import com.example.dogmeeting.repository.ChatMessageRepository;
import com.example.dogmeeting.repository.ChatRoomRepository;
import com.example.dogmeeting.repository.MatchRepository;
import com.example.dogmeeting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    @Override
    @Transactional
    public ChatRoomResponse createChatRoom(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("매치를 찾을 수 없습니다."));

        // 이미 채팅방이 있는지 확인
        ChatRoom existingChatRoom = chatRoomRepository.findByMatchId(matchId);
        if (existingChatRoom != null) {
            return ChatRoomResponse.from(existingChatRoom);
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .match(match)
                .build();

        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        return ChatRoomResponse.from(savedChatRoom);
    }

    @Override
    public ChatRoomResponse findChatRoomByMatchId(Long matchId) {
        ChatRoom chatRoom = chatRoomRepository.findByMatchId(matchId);
        return chatRoom != null ? ChatRoomResponse.from(chatRoom) : null;
    }

    @Override
    public List<ChatMessageResponse> getChatHistory(Long chatroomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        // 사용자가 해당 채팅방에 참여할 권한이 있는지 확인
        validateChatRoomAccess(chatRoom, userId);

        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderBySentAtAsc(chatroomId);

        return messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void sendMessage(ChatMessageDto messageDto) {
        try {
            User sender = userRepository.findById(messageDto.getSenderId())
                    .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

            ChatRoom chatRoom = chatRoomRepository.findById(messageDto.getChatroomId())
                    .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

            // 채팅방 접근 권한 확인
            validateChatRoomAccess(chatRoom, messageDto.getSenderId());

            // DB에 메시지 저장
            ChatMessage chatMessage = ChatMessage.builder()
                    .chatRoom(chatRoom)
                    .sender(sender)
                    .content(messageDto.getContent())
                    .read(false)
                    .build();

            chatMessageRepository.save(chatMessage);

            // 실시간 전송을 위한 DTO 설정
            messageDto.setSenderNickname(sender.getNickname());
            messageDto.setTimestamp(LocalDateTime.now());

            // 채팅방 구독자들에게 메시지 전송
            messagingTemplate.convertAndSend(
                    "/sub/chat/room/" + messageDto.getChatroomId(),
                    messageDto
            );

            log.info("메시지 저장 및 전송 완료 - 채팅방: {}, 발신자: {}", 
                    messageDto.getChatroomId(), sender.getNickname());

        } catch (Exception e) {
            log.error("메시지 전송 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void markMessagesAsRead(Long chatroomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        validateChatRoomAccess(chatRoom, userId);

        // 해당 사용자가 보낸 메시지가 아닌 읽지 않은 메시지들을 읽음 처리
        List<ChatMessage> unreadMessages = chatMessageRepository
                .findByChatRoomIdAndSenderIdNotAndReadFalse(chatroomId, userId);

        unreadMessages.forEach(ChatMessage::markAsRead);
        chatMessageRepository.saveAll(unreadMessages);

        log.info("메시지 읽음 처리 완료 - 채팅방: {}, 사용자: {}, 처리된 메시지 수: {}", 
                chatroomId, userId, unreadMessages.size());
    }

    @Override
    public int getUnreadMessageCount(Long chatroomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        validateChatRoomAccess(chatRoom, userId);

        return chatMessageRepository.countByChatRoomIdAndSenderIdNotAndReadFalse(chatroomId, userId);
    }

    private void validateChatRoomAccess(ChatRoom chatRoom, Long userId) {
        Match match = chatRoom.getMatch();
        boolean hasAccess = match.getUser1().getId().equals(userId) ||
                           match.getUser2().getId().equals(userId);

        if (!hasAccess) {
            throw new IllegalArgumentException("해당 채팅방에 접근할 권한이 없습니다.");
        }
    }

    private ChatMessageResponse convertToResponse(ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .chatroomId(message.getChatRoom().getId())
                .senderId(message.getSender().getId())
                .senderNickname(message.getSender().getNickname())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .read(message.getRead())
                .build();
    }

    @Override
    public List<ChatRoomResponse> getUserChatRooms(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        List<Match> userMatches = matchRepository.findByUser(user);
        List<ChatRoom> chatRooms = new ArrayList<>();

        for (Match match : userMatches) {
            ChatRoom chatRoom = chatRoomRepository.findByMatchId(match.getId());
            if (chatRoom != null) {
                chatRooms.add(chatRoom);
            }
        }

        return chatRooms.stream()
                .map(ChatRoomResponse::from)
                .collect(Collectors.toList());
    }
}