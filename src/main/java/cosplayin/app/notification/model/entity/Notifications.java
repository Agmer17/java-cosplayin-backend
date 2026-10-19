package cosplayin.app.notification.model.entity;

import cosplayin.app.comments.model.entity.PostsComment;
import cosplayin.app.notification.model.type.NotificationType;
import cosplayin.app.posts.model.entity.Posts;
import cosplayin.app.user.model.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_notifications_recipient"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users recipient;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private NotificationType notificationType;

    @Column(nullable = false, updatable = false)
    private String groupKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", updatable = false)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Posts posts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private PostsComment comment;

    @Column(name = "actor_count", nullable = false, updatable = false)
    private int actorCount;


    private LocalDateTime lastActivityAt;

    private LocalDateTime readAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

