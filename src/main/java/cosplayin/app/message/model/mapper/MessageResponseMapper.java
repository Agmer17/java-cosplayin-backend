package cosplayin.app.message.model.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import cosplayin.app.core.authorization.UserRoles;
import cosplayin.app.message.model.dto.MessageMediaDTO;
import cosplayin.app.message.model.dto.MessageReadResponseDTO;
import cosplayin.app.message.model.dto.MessageReplyToDTO;
import cosplayin.app.message.model.dto.MessageResponseDTO;
import cosplayin.app.profiles.model.dto.SimpleProfileDTO;
import cosplayin.app.profiles.model.type.ProfilesVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageResponseMapper implements RowMapper<MessageResponseDTO> {

    private static final TypeReference<List<MessageMediaDTO>> MEDIA_TYPE = new TypeReference<>() {};
    private static final TypeReference<List<MessageReadResponseDTO>> READS_TYPE = new TypeReference<>() {};
    private final ObjectMapper objectMapper;


    @Override
    public MessageResponseDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        SimpleProfileDTO sender = SimpleProfileDTO.builder()
                .id(rs.getObject("sender_id", UUID.class))
                .username(rs.getString("sender_username"))
                .displayName(rs.getString("sender_display_name"))
                .avatarUrl(rs.getString("sender_avatar_url"))
                .visibility(ProfilesVisibility.valueOf(rs.getString("sender_visibility")))
                .userRole(UserRoles.valueOf(rs.getString("sender_role")))
                .build();

        MessageReplyToDTO replyTo = null;
        if (rs.getObject("reply_id") != null) {
            replyTo = MessageReplyToDTO.builder()
                    .textContent(rs.getString("reply_text_content"))
                    .senderUsername(rs.getString("reply_sender_username"))
                    .mediaUrl(rs.getString("reply_media_url"))
                    .build();
        }

        return MessageResponseDTO.builder()
                .id(rs.getObject("id", UUID.class))
                .conversationId(rs.getObject("conversation_id", UUID.class))
                .sender(sender)
                .textContent(rs.getString("text_content"))
                .replyTo(replyTo)
                .media(readJson(rs.getString("media"), MEDIA_TYPE))
                .reads(readJson(rs.getString("reads"), READS_TYPE))
                .createdAt(rs.getObject("created_at", LocalDateTime.class))
                .build();
    }

    private <T> T readJson(String json, TypeReference<T> type) throws SQLException {
        try {
            return objectMapper.readValue(json, type);
        } catch (JacksonException e) {
            throw new SQLException("Failed to parse JSON column", e);
        }
    }
}
