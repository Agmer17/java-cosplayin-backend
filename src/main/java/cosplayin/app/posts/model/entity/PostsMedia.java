package cosplayin.app.posts.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import cosplayin.app.utils.storage.type.SupportedFileType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "post_media", uniqueConstraints = {
        @UniqueConstraint(name = "uk_post_display_order", columnNames = { "post_id", "display_order" })
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostsMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String mediaUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SupportedFileType mediaType;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "posts_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Posts posts;

    @Column(columnDefinition = "SMALLINT", nullable = false)
    private Short displayOrder;

    @CreationTimestamp
    private LocalDateTime createdAt;

}
