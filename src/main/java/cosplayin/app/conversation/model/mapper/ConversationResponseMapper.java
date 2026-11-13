package cosplayin.app.conversation.model.mapper;

import cosplayin.app.conversation.model.dto.ConversationLatestMessageResponse;
import cosplayin.app.conversation.model.dto.ConversationResponseDTO;
import cosplayin.app.conversation.model.dto.ParticipantResponse;
import cosplayin.app.conversation.model.type.ConversationType;
import cosplayin.app.core.exception.model.FatalErrorExceptions;
import cosplayin.app.message.model.dto.MessageReadResponseDTO;
import cosplayin.app.utils.storage.type.SupportedFileType;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class ConversationResponseMapper implements RowMapper<ConversationResponseDTO> {

    private final ObjectMapper objectMapper;

    private static final TypeReference<List<ParticipantResponse>> PARTICIPANTS_TYPE =
            new TypeReference<>() {};
    private static final TypeReference<List<MessageReadResponseDTO>> READ_BY_TYPE =
            new TypeReference<>() {};

    @Override
    public ConversationResponseDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            List<ParticipantResponse> participants =
                    objectMapper.readValue(rs.getString("participants"), PARTICIPANTS_TYPE);

            List<ConversationLatestMessageResponse> latest = new ArrayList<>();
            UUID lastMsgId = rs.getObject("lm_id", UUID.class);

            if (lastMsgId != null) {
                List<MessageReadResponseDTO> readBy =
                        objectMapper.readValue(rs.getString("lm_read_by"), READ_BY_TYPE);

                String mediaType = rs.getString("lm_media_type");

                latest.add(ConversationLatestMessageResponse.builder()
                        .id(lastMsgId)
                        .textContent(rs.getString("lm_text_content"))
                        .createdAt(rs.getObject("lm_created_at", LocalDateTime.class))
                        .firstMediaUrl(rs.getString("lm_media_url"))
                        .firstMediaType(mediaType == null ? null : SupportedFileType.valueOf(mediaType))
                        .readBy(readBy)
                        .build());
            }

            return ConversationResponseDTO.builder()
                    .id(rs.getObject("id", UUID.class))
                    .name(rs.getString("name"))
                    .conversationImage(rs.getString("conversation_pic"))
                    .convKey(rs.getString("conv_key"))
                    .createdAt(rs.getObject("created_at", LocalDateTime.class))
                    .conversationType(ConversationType.valueOf(rs.getString("conversation_type")))
                    .participants(participants)
                    .latestMessage(latest)
                    .build();

        } catch (JacksonException e) {
            throw new FatalErrorExceptions("INVALID SQL CONVERSION CHECK THIS LOG : " + e.getMessage());
        }
    }
}