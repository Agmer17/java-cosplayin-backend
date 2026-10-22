package cosplayin.app.notification.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PostsCommentRepliesNotificationEvent(
        UUID commentId,
        UUID recipient,
        UUID actorId
) {
}
