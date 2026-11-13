package cosplayin.app.conversation.model.dto;

import cosplayin.app.message.model.dto.MessageReadResponseDTO;
import cosplayin.app.utils.storage.type.SupportedFileType;
import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ConversationLatestMessageResponse {
    private UUID id;
    private String textContent;
    private LocalDateTime createdAt;
    private String firstMediaUrl;
    private SupportedFileType firstMediaType;
    private List<MessageReadResponseDTO> readBy;
}
