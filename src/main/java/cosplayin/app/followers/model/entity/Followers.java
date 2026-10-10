package cosplayin.app.followers.model.entity;

import cosplayin.app.user.model.entity.Users;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "followers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_followers_follower_following", columnNames = { "follower_id", "following_id" })
}, indexes = {
        @Index(name = "idx_followers_follower_id", columnList = "follower_id"),
        @Index(name = "idx_followers_following_id", columnList = "following_id")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Followers {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users follower;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "following_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users following;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}