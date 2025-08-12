package com.example.dogmeeting.controller;

import com.example.dogmeeting.dto.ChatMessageDto;
import com.example.dogmeeting.dto.MessageType;
import com.example.dogmeeting.entity.ChatMessage;
import com.example.dogmeeting.entity.ChatRoom;
import com.example.dogmeeting.entity.User;
import com.example.dogmeeting.exception.UserNotFoundException;
import com.example.dogmeeting.repository.ChatMessageRepository;
import com.example.dogmeeting.repository.ChatRoomRepository;
import com.example.dogmeeting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @MessageMapping("/chat/message")
    public void message(ChatMessageDto messageDto) {
        try {
            // 사용자 정보 조회
            User sender = userRepository.findByUserId(messageDto.getSenderLoginId())
                    .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + messageDto.getSenderLoginId()));

            // 닉네임 설정
            messageDto.setSenderNickname(sender.getNickname());
            messageDto.setTimestamp(LocalDateTime.now());

            // 채팅 메시지 타입에 따른 처리
            if (messageDto.getType() == MessageType.CHAT) {
                // 일반 채팅 메시지인 경우 DB에 저장
                saveChatMessage(messageDto, sender);
            }

            // 해당 채팅방을 구독한 클라이언트들에게 메시지 전송
            messagingTemplate.convertAndSend(
                    "/sub/chat/room/" + messageDto.getChatroomId(),
                    messageDto
            );

            log.info("메시지 전송 완료 - 채팅방: {}, 발신자: {}, 내용: {}",
                    messageDto.getChatroomId(), messageDto.getSenderNickname(), messageDto.getContent());

        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    @MessageMapping("/chat/enter")
    public void enterUser(ChatMessageDto messageDto) {
        try {
            User user = userRepository.findByUserId(messageDto.getSenderLoginId())
                    .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + messageDto.getSenderLoginId()));

            messageDto.setSenderNickname(user.getNickname());
            messageDto.setType(MessageType.ENTER);
            messageDto.setContent(user.getNickname() + "님이 입장하셨습니다.");
            messageDto.setTimestamp(LocalDateTime.now());

            messagingTemplate.convertAndSend(
                    "/sub/chat/room/" + messageDto.getChatroomId(),
                    messageDto
            );

            log.info("사용자 입장 - 채팅방: {}, 사용자: {}", messageDto.getChatroomId(), user.getNickname());

        } catch (Exception e) {
            log.error("사용자 입장 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    @MessageMapping("/chat/leave")
    public void leaveUser(ChatMessageDto messageDto) {
        try {
            User user = userRepository.findByUserId(messageDto.getSenderLoginId())
                    .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다: " + messageDto.getSenderLoginId()));

            messageDto.setSenderNickname(user.getNickname());
            messageDto.setType(MessageType.LEAVE);
            messageDto.setContent(user.getNickname() + "님이 퇴장하셨습니다.");
            messageDto.setTimestamp(LocalDateTime.now());

            messagingTemplate.convertAndSend(
                    "/sub/chat/room/" + messageDto.getChatroomId(),
                    messageDto
            );

            log.info("사용자 퇴장 - 채팅방: {}, 사용자: {}", messageDto.getChatroomId(), user.getNickname());

        } catch (Exception e) {
            log.error("사용자 퇴장 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private void saveChatMessage(ChatMessageDto messageDto, User sender) {
        try {
            ChatRoom chatRoom = chatRoomRepository.findById(messageDto.getChatroomId())
                    .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

            ChatMessage chatMessage = ChatMessage.builder()
                    .chatRoom(chatRoom)
                    .sender(sender)
                    .content(messageDto.getContent())
                    .read(false)
                    .build();

            chatMessageRepository.save(chatMessage);

        } catch (Exception e) {
            log.error("채팅 메시지 저장 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
}