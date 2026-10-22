package cosplayin.app.notification.event;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PostsCommentNotificationEvent(
       UUID postsId,
       UUID actorId,
       UUID commentId,
       UUID recipient
) {
}
