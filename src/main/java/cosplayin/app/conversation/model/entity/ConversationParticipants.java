package cosplayin.app.conversation.model.entity;

import cosplayin.app.conversation.model.type.ConversationRoles;
import cosplayin.app.user.model.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "conversation_participants",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_conversation_participant",
                        columnNames = {"user_id", "conversation_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_participant_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_participant_conversation",
                        columnList = "conversation_id"
                )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversationParticipants {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ConversationRoles conversationRole = ConversationRoles.USER;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
