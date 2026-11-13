package cosplayin.app.message.model.entity;

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
        name = "message_reads",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_message_read_user",
                        columnNames = {"message_id", "user_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_message_read_message",
                        columnList = "message_id"
                ),
                @Index(
                        name = "idx_message_read_user",
                        columnList = "user_id"
                )
        }
)


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MessageRead {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users user;

    @CreationTimestamp
    private LocalDateTime readAt;
}
