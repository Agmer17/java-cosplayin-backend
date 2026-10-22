package cosplayin.app.notification.event;

import java.util.UUID;

public record PostCommentLikeNotificationEvent(
        UUID commentId,
        UUID actorId,
        UUID recipient
) {
}
