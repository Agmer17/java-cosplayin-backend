package cosplayin.app.notification.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class NotificationActorId {
    @Column(nullable = false)
    private UUID notificationId;

    @Column(nullable = false)
    private UUID actorId;
}
