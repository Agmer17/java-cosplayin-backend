package cosplayin.app.notification.event;

import java.util.UUID;

public record FollowNotificationEvent(
        UUID actorId,
        UUID recipient
) {
}
