package cosplayin.app.message.listener;

import cosplayin.app.message.event.GroupMessageCreatedEvent;
import cosplayin.app.message.event.PrivateMessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class MessageWebsocketListener {
    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onPrivateMessageCreate(PrivateMessageCreatedEvent event) {
        messagingTemplate.convertAndSendToUser(event.sender().toString(), "/personal/private-message", event.message());
        messagingTemplate.convertAndSendToUser(event.target().toString(), "/personal/private-message", event.message());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onGroupMessageCreate(GroupMessageCreatedEvent event) {
        event.receiver().forEach(mem -> messagingTemplate.convertAndSendToUser(mem.toString(), "/personal/group-chat", event.messageResponseDTO()));
    }
}
