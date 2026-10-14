package cosplayin.app.notification.model.entity;

import cosplayin.app.notification.model.type.NotificationTargetType;
import cosplayin.app.notification.model.type.NotificationTypes;
import cosplayin.app.user.model.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications", indexes = {
        @Index(
                name = "idx_notif_recipient_created",
                columnList = "recipient_id, created_at DESC, id DESC"
        )},
        uniqueConstraints = {
        @UniqueConstraint(
                name = "uq_notif_event",
                columnNames = {"recipient_id", "actor_id", "notificationType", "targetType", "targetId"}
                // Pastikan penamaan columnNames sesuai dengan nama kolom di database.
                // Jika Anda menggunakan penamaan snake_case di DB, ubah menjadi:
                // columnNames = {"recipient_id", "actor_id", "notification_type", "target_type", "target_id"}
        )
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users actor;

    @Column(nullable = false)
    private NotificationTypes notificationType;

    @Column(nullable = false)
    private NotificationTargetType targetType;

    @Column(nullable = false)
    private UUID targetId;

    @Column(nullable = false)
    private String payload;

    private LocalDateTime readAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
