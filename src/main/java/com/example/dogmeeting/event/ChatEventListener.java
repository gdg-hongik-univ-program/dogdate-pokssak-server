package com.example.dogmeeting.event;

import com.example.dogmeeting.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatEventListener {

    private final ChatService chatService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMatchSuccess(MatchSuccessEvent event) {
        log.info("매칭 성사 이벤트 수신 - matchId: {}", event.matchId());
        chatService.createChatRoom(event.matchId());
    }
}