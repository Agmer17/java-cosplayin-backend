package cosplayin.app.notification.event;

import cosplayin.app.followers.model.type.FollowStatus;

import java.util.UUID;

public record FollowRequestNotificationEvent(
        UUID curr,
        UUID target,
        UUID requestId
) {
}
