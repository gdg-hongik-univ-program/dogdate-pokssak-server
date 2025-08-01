package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.ChatMessageDto;
import com.example.dogmeeting.dto.ChatMessageResponse;
import com.example.dogmeeting.entity.ChatRoom;

import java.util.List;

public interface ChatService {
    
    ChatRoom createChatRoom(Long matchId);
    
    ChatRoom findChatRoomByMatchId(Long matchId);
    
    List<ChatMessageResponse> getChatHistory(Long chatroomId, Long userId);
    
    void sendMessage(ChatMessageDto messageDto);
    
    void markMessagesAsRead(Long chatroomId, Long userId);
    
    int getUnreadMessageCount(Long chatroomId, Long userId);
}