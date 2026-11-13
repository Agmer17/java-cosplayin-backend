package cosplayin.app.message.repository;

import cosplayin.app.message.model.dto.MessageResponseDTO;
import cosplayin.app.message.model.mapper.MessageResponseMapper;
import lombok.RequiredArgsConstructor;
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
public class MessageJdbcQuery {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final MessageResponseMapper mapper;

    private static final String BASE_SELECT = """
            SELECT m.id,
                   m.conversation_id,
                   m.text_content,
                   m.created_at,

                   u.id            AS sender_id,
                   u.username      AS sender_username,
                   u.role          AS sender_role,
                   p.display_name  AS sender_display_name,
                   p.avatar_url    AS sender_avatar_url,
                   p.visibility    AS sender_visibility,

                   rm.id           AS reply_id,
                   rm.text_content AS reply_text_content,
                   ru.username     AS reply_sender_username,
                   (
                       SELECT ra.media_url
                       FROM chat_attachments ra
                       WHERE ra.chat_id = rm.id
                       ORDER BY ra.media_order
                       LIMIT 1
                   ) AS reply_media_url,

                   COALESCE((
                       SELECT jsonb_agg(
                           jsonb_build_object(
                               'id', a.id,
                               'media_url', a.media_url,
                               'media_type', a.media_type,
                               'media_order', a.media_order
                           ) ORDER BY a.media_order
                       )
                       FROM chat_attachments a
                       WHERE a.chat_id = m.id
                   ), '[]'::jsonb) AS media,

                   COALESCE((
                       SELECT jsonb_agg(
                           jsonb_build_object(
                               'user_id', r.user_id,
                               'read_at', r.read_at
                           )
                       )
                       FROM message_reads r
                       WHERE r.message_id = m.id
                   ), '[]'::jsonb) AS reads

            FROM chats m
            JOIN users u ON u.id = m.sender_id
            JOIN profiles p ON p.id = u.id
            LEFT JOIN chats rm ON rm.id = m.reply_to_message_id
            LEFT JOIN users ru ON ru.id = rm.sender_id
            """;

    public Optional<MessageResponseDTO> findById(UUID messageId) {
        var sql = BASE_SELECT + """
                WHERE m.id = :messageId
                LIMIT 1
                """;

        var params = new MapSqlParameterSource()
                .addValue("messageId", messageId);

        return jdbcTemplate.query(sql, params, mapper)
                .stream()
                .findFirst();
    }

    public List<MessageResponseDTO> findByConversation(UUID conversationId) {
        var sql = BASE_SELECT + """
            WHERE m.conversation_id = :conversationId
            ORDER BY m.created_at ASC
            """;

        var params = new MapSqlParameterSource()
                .addValue("conversationId", conversationId);

        return jdbcTemplate.query(sql, params, mapper);
    }
}