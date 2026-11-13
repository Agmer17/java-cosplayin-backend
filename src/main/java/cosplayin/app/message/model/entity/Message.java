package cosplayin.app.message.model.entity;

import cosplayin.app.conversation.model.entity.Conversation;
import cosplayin.app.user.model.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "chats",
        indexes = {
                @Index(
                        name = "idx_chat_conversation_created",
                        columnList = "conversation_id, created_at"
                ),
                @Index(
                        name = "idx_chat_sender",
                        columnList = "sender_id"
                )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "conversation_id",
            nullable = false,
            updatable = false
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Conversation conversation;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "sender_id",
            nullable = false,
            updatable = false
    )
    private Users sender;

    @Column(columnDefinition = "TEXT")
    private String textContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_message_id")
    private Message replyTo;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
