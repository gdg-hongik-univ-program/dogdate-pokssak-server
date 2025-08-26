package com.example.dogmeeting.service;

import com.example.dogmeeting.dto.ChatMessageDto;
import com.example.dogmeeting.dto.ChatMessageResponse;
import com.example.dogmeeting.dto.ChatRoomResponse;

import java.util.List;

public interface ChatService {
    
    ChatRoomResponse createChatRoom(Long matchId);
    
    ChatRoomResponse findChatRoomByMatchId(Long matchId);
    
    List<ChatMessageResponse> getChatHistory(Long chatroomId, Long userId);
    
    void sendMessage(ChatMessageDto messageDto);
    
    void markMessagesAsRead(Long chatroomId, Long userId);
    
    int getUnreadMessageCount(Long chatroomId, Long userId);

    List<ChatRoomResponse> getUserChatRooms(Long userId);

    ChatMessageResponse getLastMessage(Long chatroomId);
}
