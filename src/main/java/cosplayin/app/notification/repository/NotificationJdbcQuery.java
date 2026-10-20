package cosplayin.app.notification.repository;

import cosplayin.app.notification.model.dto.NotificationResponse;
import cosplayin.app.notification.model.mapper.NotificationResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NotificationJdbcQuery {

    private static final String BASE_SELECT = """
            SELECT n.id,
                   n.notification_type,
                   n.actor_count,
                   n.created_at,
                   n.last_activity_at,
                   n.read_at,
                   n.post_id,
                   pm.media_url                    AS post_thumbnail,
                   c.id                            AS comment_id,
                   c.comment                       AS comment_text,
                   c.created_at                    AS comment_created_at,
                   COALESCE(a.actors, '[]'::json)  AS actors
            FROM notification n
            LEFT JOIN post_comment c ON c.id = n.comment_id
            LEFT JOIN post_media pm
                   ON pm.posts_id = n.post_id
                  AND pm.display_order = 0
            LEFT JOIN LATERAL (
                SELECT json_agg(
                           json_build_object(
                               'user_id',         t.id,
                               'username',        t.username,
                               'profile_picture', t.avatar_url
                           ) ORDER BY t.actor_at DESC
                       ) AS actors
                FROM (
                    SELECT u.id, u.username, p.avatar_url, na.created_at AS actor_at
                    FROM notification_actor na
                    JOIN users u    ON u.id = na.actor_id
                    JOIN profiles p ON p.id = u.id
                    WHERE na.notification_id = n.id
                    ORDER BY na.created_at DESC
                    LIMIT 3
                ) t
            ) a ON TRUE
            """;

    private static final String SQL_LIST = BASE_SELECT + """
            WHERE n.recipient_id = :recipientId
              AND (CAST(:cursorTime AS timestamp) IS NULL
                   OR (n.last_activity_at, n.id) < (CAST(:cursorTime AS timestamp), CAST(:cursorId AS uuid)))
            ORDER BY n.last_activity_at DESC, n.id DESC
            LIMIT :limit
            """;

    private static final String SQL_BY_ID = BASE_SELECT + """
            WHERE n.id = :id
              AND n.recipient_id = :recipientId
            """;

    private final NamedParameterJdbcTemplate jdbc;
    private final NotificationResponseMapper rowMapper;

    public List<NotificationResponse> findByRecipient(
            UUID recipientId, LocalDateTime cursorTime, UUID cursorId, int limit) {

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("recipientId", recipientId)
                .addValue("cursorTime", cursorTime, Types.TIMESTAMP)
                .addValue("cursorId", cursorId, Types.OTHER)
                .addValue("limit", limit);

        return jdbc.query(SQL_LIST, params, rowMapper);
    }

    public Optional<NotificationResponse> findById(UUID notificationId, UUID recipientId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", notificationId)
                .addValue("recipientId", recipientId);

        try {
            return Optional.ofNullable(jdbc.queryForObject(SQL_BY_ID, params, rowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

}
