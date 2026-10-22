package cosplayin.app.notification.listener;

import cosplayin.app.notification.event.*;
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
    public void onPostsComment(PostsCommentNotificationEvent event){
        service.createNotification(event);
    }

    @EventListener
    @Async
    public void onPostsCommentLike(PostCommentLikeNotificationEvent event){

        service.createNotification(event);
    }


    @EventListener
    @Async
    public void onPostsCommentReplies(PostsCommentRepliesNotificationEvent event){

        service.createNotification(event);
    }

    @EventListener
    @Async
    public void onFollow(FollowNotificationEvent event){
        service.createNotification(event);
    }

}
