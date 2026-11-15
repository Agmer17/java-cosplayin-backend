package cosplayin.app.notification.event;

import java.util.UUID;

public record RemoveFollowRequestNotification(
        UUID requestId
) {
}
