package cosplayin.app.likes.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import cosplayin.app.posts.model.entity.Posts;
import cosplayin.app.user.model.entity.Users;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "posts_likes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_posts_user_id", columnNames = {
                "user_id", "posts_id"
        })
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostsLikes {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "posts_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Posts posts;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Users user;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
