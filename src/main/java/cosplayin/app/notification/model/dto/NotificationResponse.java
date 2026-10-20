package cosplayin.app.notification.model.dto;

import cosplayin.app.notification.model.type.NotificationType;
import lombok.Builder;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record NotificationResponse(
        UUID id,
        PostsPreviewNotificationDTO postPreview,
        CommentPreviewDTO commentPreview,
        NotificationType notificationType,
        List<ActorNotificationPreview> actors,
        long actorCount,
        LocalDateTime createdAt,
        LocalDateTime lastActivityAt,
        LocalDateTime readAt

) {
}
