package cosplayin.app.notification.listener;

import cosplayin.app.notification.event.PostsLikeEvent;
import cosplayin.app.notification.event.RemovePostsLikeEvent;
import cosplayin.app.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final NotificationService service;

    @EventListener
    @Async
    public void onPostsLike(PostsLikeEvent event) {
        service.createNotification(event);
    }

    @EventListener
    @Async
    public void onPostsLikeRemove(RemovePostsLikeEvent event) {
        service.removeNotificationsActor(event);
    }
}
