package cosplayin.app.notification.event;
import cosplayin.app.posts.model.entity.Posts;

import java.util.UUID;

public record PostsLikeEvent(
        UUID actorId,
        Posts posts
) {
}
