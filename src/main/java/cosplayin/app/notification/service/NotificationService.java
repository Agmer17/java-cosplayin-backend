package cosplayin.app.notification.service;


import cosplayin.app.comments.model.entity.PostsComment;
import cosplayin.app.notification.event.*;
import cosplayin.app.notification.model.dto.NotificationResponse;
import cosplayin.app.notification.model.entity.NotificationActor;
import cosplayin.app.notification.model.entity.Notifications;
import cosplayin.app.notification.model.type.NotificationType;
import cosplayin.app.notification.repository.NotificationActorRepository;
import cosplayin.app.notification.repository.NotificationJdbcQuery;
import cosplayin.app.notification.repository.NotificationRepository;
import cosplayin.app.posts.model.entity.Posts;
import cosplayin.app.user.model.entity.Users;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationActorRepository actorRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationJdbcQuery notificationJdbcQuery;

    public String NotificationGroupKeyOf(@NonNull NotificationType type, @NonNull UUID targetId) {
        return type.name() + ":" + targetId;
    }

    private void saveNotification(Users actor, Users recipient, Notifications newNotif) {
        notificationRepository.save(newNotif);

        NotificationActor notifActor = NotificationActor.builder()
                .notifications(newNotif)
                .actor(actor)
                .build();

        actorRepository.save(notifActor);

        Optional<NotificationResponse> response = notificationJdbcQuery.findById(newNotif.getId(), recipient.getId());

        response.ifPresent(notificationResponse -> messagingTemplate.convertAndSendToUser(recipient.getId().toString(), "/personal/notifications", notificationResponse));
    }

private void saveActorToNotification(Optional<Notifications> existed, Users actor, Users recipient) {

        if (existed.isEmpty()) return;

        NotificationActor notifActor = NotificationActor.builder()
                .notifications(existed.get())
                .actor(actor)
                .build();

        actorRepository.save(notifActor);

        Notifications notif = existed.get();

        notif.setActorCount(notif.getActorCount() + 1);

        Optional<NotificationResponse> response = notificationJdbcQuery.findById(existed.get().getId(), recipient.getId());
        response.ifPresent(notificationResponse -> messagingTemplate.convertAndSendToUser(recipient.getId().toString(), "/personal/notifications", notificationResponse));
    }


    @Transactional
    public void createNotification(PostsLikeEvent event) {
        String groupKey = this.NotificationGroupKeyOf(NotificationType.POST_LIKE, event.posts().getId());
        Optional<Notifications> existed = notificationRepository.findByGroupKey(groupKey);

        Users actor = Users.builder().id(event.actorId()).build();
        Users recipient = Users.builder().id(event.posts().getAuthor().getId()).build();

        if (existed.isEmpty()) {

            Notifications newNotif = Notifications  .builder()
                    .groupKey(groupKey)
                    .recipient(recipient)
                    .actorCount(1)
                    .posts(event.posts())
                    .notificationType(NotificationType.POST_LIKE)
                    .build();


            saveNotification(actor, recipient, newNotif);


        } else {
            saveActorToNotification(existed, actor, recipient);


        }



    }

    @Transactional
    public void createNotification(PostsCommentNotificationEvent event) {
        String groupKey = this.NotificationGroupKeyOf(NotificationType.POST_COMMENT, event.commentId());

        Optional<Notifications> existed = notificationRepository.findByGroupKey(groupKey);
        Users actor = Users.builder().id(event.actorId()).build();
        Users recipient = Users.builder().id(event.recipient()).build();

        if (existed.isEmpty()) {
            PostsComment comment = PostsComment.builder().id(event.commentId()).build();
            Posts posts = Posts.builder().id(event.postsId()).build();



            Notifications newNotif = Notifications .builder()
                    .groupKey(groupKey)
                    .recipient(recipient)
                    .actorCount(1)
                    .comment(comment)
                    .posts(posts)
                    .notificationType(NotificationType.POST_COMMENT)
                    .build();


            saveNotification(actor, recipient, newNotif);


        } else {

            saveActorToNotification(existed, actor, recipient);


        }

    }

    public void createNotification(PostCommentLikeNotificationEvent event) {
        String groupKey = this.NotificationGroupKeyOf(NotificationType.COMMENT_LIKE, event.commentId());
        Optional<Notifications> existed = notificationRepository.findByGroupKey(groupKey);

        Users actor = Users.builder().id(event.actorId()).build();
        Users recipient = Users.builder().id(event.recipient()).build();

        if (existed.isEmpty()) {
            PostsComment comment = PostsComment.builder().id(event.commentId()).build();
            Notifications newNotif = Notifications .builder()
                    .groupKey(groupKey)
                    .recipient(recipient)
                    .actorCount(1)
                    .comment(comment)
                    .notificationType(NotificationType.COMMENT_LIKE)
                    .build();

            saveNotification(actor, recipient, newNotif);

        } else {
            saveActorToNotification(existed, actor, recipient);

        }
    }

    public void createNotification(PostsCommentRepliesNotificationEvent event){
        String groupKey = this.NotificationGroupKeyOf(NotificationType.COMMENT_REPLY, event.commentId());

        Optional<Notifications> existed = notificationRepository.findByGroupKey(groupKey);

        Users actor = Users.builder().id(event.actorId()).build();
        Users recipient = Users.builder().id(event.recipient()).build();

        if (existed.isEmpty()) {
            PostsComment comment = PostsComment.builder().id(event.commentId()).build();
            Notifications newNotif = Notifications .builder()
                    .groupKey(groupKey)
                    .recipient(recipient)
                    .actorCount(1)
                    .comment(comment)
                    .notificationType(NotificationType.COMMENT_REPLY)
                    .build();

            saveNotification(actor, recipient, newNotif);

        } else {
            saveActorToNotification(existed, actor, recipient);

        }
    }


    public void createNotification(FollowNotificationEvent event) {
        String groupKey = this.NotificationGroupKeyOf(NotificationType.FOLLOW, event.recipient());

        Optional<Notifications> existed = notificationRepository.findByGroupKey(groupKey);

        Users actor = Users.builder().id(event.actorId()).build();
        Users recipient = Users.builder().id(event.recipient()).build();

        if (existed.isEmpty()) {
            Notifications newNotif = Notifications .builder()
                    .groupKey(groupKey)
                    .recipient(recipient)
                    .actorCount(1)
                    .notificationType(NotificationType.FOLLOW)
                    .build();

            saveNotification(actor, recipient, newNotif);

        } else {
            saveActorToNotification(existed, actor, recipient);

        }
    }


    public List<NotificationResponse> getNotificationFromUsers(UUID userId) {
            return notificationJdbcQuery.findByRecipient(userId);

    }

}
