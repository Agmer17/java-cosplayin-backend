package cosplayin.app.notification.listener;

import cosplayin.app.notification.event.*;
import cosplayin.app.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
@RequiredArgsConstructor
public class NotificationEventListener {
    private final NotificationService service;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onPostsLike(PostsLikeEvent event) {
        service.createNotification(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onPostsComment(PostsCommentNotificationEvent event){
        service.createNotification(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onPostsCommentLike(PostCommentLikeNotificationEvent event){

        service.createNotification(event);
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onPostsCommentReplies(PostsCommentRepliesNotificationEvent event){

        service.createNotification(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onFollow(FollowNotificationEvent event){
        service.createNotification(event);
    }

}
