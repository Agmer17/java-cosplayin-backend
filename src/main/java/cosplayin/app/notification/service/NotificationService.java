package cosplayin.app.notification.service;


import cosplayin.app.notification.event.PostsLikeEvent;
import cosplayin.app.notification.event.RemovePostsLikeEvent;
import cosplayin.app.notification.model.dto.NotificationResponse;
import cosplayin.app.notification.model.entity.NotificationActor;
import cosplayin.app.notification.model.entity.NotificationActorId;
import cosplayin.app.notification.model.entity.Notifications;
import cosplayin.app.notification.model.type.NotificationType;
import cosplayin.app.notification.repository.NotificationActorRepository;
import cosplayin.app.notification.repository.NotificationJdbcQuery;
import cosplayin.app.notification.repository.NotificationRepository;
import cosplayin.app.user.model.entity.Users;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    @Transactional
    public void createNotification(PostsLikeEvent event) {
        String groupKey = this.NotificationGroupKeyOf(NotificationType.POST_LIKE, event.posts().getId());
        Optional<Notifications> existed = notificationRepository.findByGroupKey(groupKey);

        Users actor = Users.builder().id(event.actorId()).build();
        Users recipient = Users.builder().id(event.posts().getAuthor().getId()).build();

        if (existed.isEmpty()) {

            Notifications newNotif = Notifications.builder()
                    .groupKey(groupKey)
                    .recipient(recipient)
                    .actorCount(1)
                    .posts(event.posts())
                    .notificationType(NotificationType.POST_LIKE)
                    .build();

            notificationRepository.save(newNotif);

            NotificationActor notifActor = NotificationActor.builder()
                    .notifications(newNotif)
                    .actor(actor)
                    .build();

            actorRepository.save(notifActor);

            Optional<NotificationResponse> response = notificationJdbcQuery.findById(newNotif.getId(), recipient.getId());

            response.ifPresent(notificationResponse -> messagingTemplate.convertAndSendToUser(recipient.getId().toString(), "/personal/notifications", notificationResponse));


        } else {
            NotificationActor notifActor = NotificationActor.builder()
                    .notifications(existed.get())
                    .actor(actor)
                    .build();

            actorRepository.save(notifActor);

            Notifications notif = existed.get();

            notif.setActorCount(notif.getActorCount() + 1);

            Optional<NotificationResponse> response = notificationJdbcQuery.findById(existed.get().getId(), recipient.getId());
//            response.ifPresent(notificationResponse -> messagingTemplate.convertAndSendToUser(recipient.getId().toString(), "/personal/notifications", notificationResponse));
        }



    }


    @Transactional
    public void removeNotificationsActor(RemovePostsLikeEvent event) {
        Optional<Notifications> notif = notificationRepository.findByGroupKey(this.NotificationGroupKeyOf(NotificationType.POST_LIKE, event.postsId()));

        if (notif.isEmpty()) return;

        actorRepository.deleteById(new NotificationActorId(
                    notif.get().getId(),
                    event.userId()
                ));

        notif.get().setActorCount(notif.get().getActorCount() - 1);
    }



}
