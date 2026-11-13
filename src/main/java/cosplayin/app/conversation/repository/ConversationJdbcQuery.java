package cosplayin.app.conversation.repository;

import cosplayin.app.conversation.model.dto.ConversationResponseDTO;
import cosplayin.app.conversation.model.mapper.ConversationResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ConversationJdbcQuery {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ConversationResponseMapper mapper;

    private static final String BASE_SELECT = """
            SELECT c.id,
                   c.name,
                   c.conversation_pic,
                   c.conv_key,
                   c.created_at,
                   c.conversation_type,

                   COALESCE((
                       SELECT jsonb_agg(
                           jsonb_build_object(
                               'id', cp.id,
                               'role', cp.conversation_role,
                               'user', jsonb_build_object(
                                   'id', u.id,
                                   'username', u.username,
                                   'role', u.role,
                                   'display_name', p.display_name,
                                   'avatar_url', p.avatar_url,
                                   'visibility', p.visibility
                               )
                           ) ORDER BY cp.created_at
                       )
                       FROM conversation_participants cp
                       JOIN users u ON u.id = cp.user_id
                       JOIN profiles p ON p.id = u.id
                       WHERE cp.conversation_id = c.id
                   ), '[]'::jsonb) AS participants,

                   lm.id           AS lm_id,
                   lm.text_content AS lm_text_content,
                   lm.created_at   AS lm_created_at,
                   (
                       SELECT a.media_url
                       FROM chat_attachments a
                       WHERE a.chat_id = lm.id
                       ORDER BY a.media_order
                       LIMIT 1
                   ) AS lm_media_url,
                   (
                       SELECT a.media_type
                       FROM chat_attachments a
                       WHERE a.chat_id = lm.id
                       ORDER BY a.media_order
                       LIMIT 1
                   ) AS lm_media_type,
                   COALESCE((
                       SELECT jsonb_agg(
                           jsonb_build_object(
                               'user_id', r.user_id,
                               'read_at', r.read_at
                           )
                       )
                       FROM message_reads r
                       WHERE r.message_id = lm.id
                   ), '[]'::jsonb) AS lm_read_by

            FROM conversations c
            LEFT JOIN LATERAL (
                SELECT m.id, m.text_content, m.created_at
                FROM chats m
                WHERE m.conversation_id = c.id
                ORDER BY m.created_at DESC
                LIMIT 1
            ) lm ON TRUE
            """;

    public Optional<ConversationResponseDTO> findByConvKey(String convKey) {
        var sql = BASE_SELECT + """
                WHERE c.conv_key = :convKey
                LIMIT 1
                """;

        var params = new MapSqlParameterSource()
                .addValue("convKey", convKey);

        return jdbcTemplate.query(sql, params, mapper)
                .stream()
                .findFirst();
    }

    public List<ConversationResponseDTO> findByUser(UUID userId) {
        var sql = BASE_SELECT + """
                WHERE EXISTS (
                    SELECT 1
                    FROM conversation_participants me
                    WHERE me.conversation_id = c.id
                      AND me.user_id = :userId
                )
                ORDER BY COALESCE(lm.created_at, c.created_at) DESC
                """;

        var params = new MapSqlParameterSource()
                .addValue("userId", userId);

        return jdbcTemplate.query(sql, params, mapper);
    }
}
