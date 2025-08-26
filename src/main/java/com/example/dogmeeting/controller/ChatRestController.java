package com.example.dogmeeting.controller;

import com.example.dogmeeting.dto.ChatMessageResponse;
import com.example.dogmeeting.dto.ChatRoomResponse;
import com.example.dogmeeting.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    @GetMapping("/{chatroomId}/history")
    public ResponseEntity<List<ChatMessageResponse>> getChatHistory(
            @PathVariable Long chatroomId,
            @RequestParam Long userId) {
        List<ChatMessageResponse> messages = chatService.getChatHistory(chatroomId, userId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{chatroomId}/unread-count")
    public ResponseEntity<Map<String, Integer>> getUnreadMessageCount(
            @PathVariable Long chatroomId,
            @RequestParam Long userId) {
        int unreadCount = chatService.getUnreadMessageCount(chatroomId, userId);
        Map<String, Integer> response = new HashMap<>();
        response.put("unreadCount", unreadCount);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{chatroomId}/read")
    public ResponseEntity<String> markMessagesAsRead(
            @PathVariable Long chatroomId,
            @RequestParam Long userId) {
        chatService.markMessagesAsRead(chatroomId, userId);
        return ResponseEntity.ok("메시지를 읽음 처리했습니다.");
    }

    @GetMapping("/room/match/{matchId}")
    public ResponseEntity<ChatRoomResponse> getChatRoomByMatch(@PathVariable Long matchId) {
        ChatRoomResponse chatRoom = chatService.findChatRoomByMatchId(matchId);
        if (chatRoom != null) {
            return ResponseEntity.ok(chatRoom);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/users/{userId}/chatrooms")
    public ResponseEntity<List<ChatRoomResponse>> getUserChatRooms(@PathVariable Long userId) {
        List<ChatRoomResponse> chatRooms = chatService.getUserChatRooms(userId);
        return ResponseEntity.ok(chatRooms);
    }

    @GetMapping("/{chatroomId}/last-message")
    public ResponseEntity<ChatMessageResponse> getLastMessage(
            @PathVariable Long chatroomId) {
        ChatMessageResponse lastMessage = chatService.getLastMessage(chatroomId);
        if (lastMessage != null) {
            return ResponseEntity.ok(lastMessage);
        }
        return ResponseEntity.noContent().build(); // 메시지가 없을 경우
    }
}
