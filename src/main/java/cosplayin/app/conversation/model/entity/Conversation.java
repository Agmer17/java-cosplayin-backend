package cosplayin.app.conversation.model.entity;

import cosplayin.app.conversation.model.type.ConversationType;
import cosplayin.app.user.model.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "conversations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private ConversationType conversationType;

    @Column(nullable = false, updatable = false, unique = true)
    private String convKey;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private Users createdBy;

    private String conversationPic;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
