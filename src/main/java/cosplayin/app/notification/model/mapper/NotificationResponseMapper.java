package cosplayin.app.notification.model.mapper;

import cosplayin.app.notification.model.dto.ActorNotificationPreview;
import cosplayin.app.notification.model.dto.CommentPreviewDTO;
import cosplayin.app.notification.model.dto.NotificationResponse;
import cosplayin.app.notification.model.dto.PostsPreviewNotificationDTO;
import cosplayin.app.notification.model.type.NotificationType;
import cosplayin.app.utils.SignerUrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationResponseMapper implements RowMapper<NotificationResponse> {
    private static final TypeReference<List<ActorNotificationPreview>> ACTORS_TYPE =
            new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    private final SignerUrlUtils signer;

    @Value("${storage.s3.bucket.private}")
    private String privateBucket;



    @Override
    public NotificationResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID postId = rs.getObject("post_id", UUID.class);
        UUID commentId = rs.getObject("comment_id", UUID.class);

        PostsPreviewNotificationDTO postPreview = postId == null ? null
                : PostsPreviewNotificationDTO.builder()
                .postId(postId)
                .thumbnail(signer.sign(privateBucket, rs.getString("post_thumbnail"), Duration.ofHours(1)))
                .fileType(rs.getString("media_type"))
                .build();

        CommentPreviewDTO commentPreview = commentId == null ? null
                : CommentPreviewDTO.builder()
                .comment(rs.getString("comment_text"))
                .createdAt(rs.getObject("comment_created_at", LocalDateTime.class))
                .build();

        List<ActorNotificationPreview> actors;
        actors = objectMapper.readValue(rs.getString("actors"), ACTORS_TYPE);

        return NotificationResponse.builder()
                .id(rs.getObject("id", UUID.class))
                .postPreview(postPreview)
                .commentPreview(commentPreview)
                .notificationType(NotificationType.valueOf(rs.getString("notification_type")))
                .actors(actors)
                .actorCount(rs.getLong("actor_count"))
                .createdAt(rs.getObject("created_at", LocalDateTime.class))
                .lastActivityAt(rs.getObject("last_activity_at", LocalDateTime.class))
                .readAt(rs.getObject("read_at", LocalDateTime.class))
                .build();
    }
}
