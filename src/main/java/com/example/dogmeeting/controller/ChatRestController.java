package com.example.dogmeeting.controller;

import com.example.dogmeeting.dto.ChatMessageResponse;
import com.example.dogmeeting.entity.ChatRoom;
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

    @PostMapping("/room/match/{matchId}")
    public ResponseEntity<ChatRoom> createChatRoom(@PathVariable Long matchId) {
        ChatRoom chatRoom = chatService.createChatRoom(matchId);
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping("/room/match/{matchId}")
    public ResponseEntity<ChatRoom> getChatRoomByMatch(@PathVariable Long matchId) {
        ChatRoom chatRoom = chatService.findChatRoomByMatchId(matchId);
        if (chatRoom != null) {
            return ResponseEntity.ok(chatRoom);
        }
        return ResponseEntity.notFound().build();
    }
}