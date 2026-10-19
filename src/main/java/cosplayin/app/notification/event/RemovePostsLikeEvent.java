package cosplayin.app.notification.event;

import java.util.UUID;

public record RemovePostsLikeEvent(
        UUID postsId,
        UUID userId
) {
}
