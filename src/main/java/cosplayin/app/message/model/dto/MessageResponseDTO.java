package cosplayin.app.message.model.dto;

import cosplayin.app.profiles.model.dto.SimpleProfileDTO;
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
public class MessageResponseDTO {
    private UUID id;
    private UUID conversationId;
    private SimpleProfileDTO sender;
    private String textContent;
    private MessageReplyToDTO replyTo;
    private List<MessageMediaDTO> media;
    private List<MessageReadResponseDTO> reads;
    private LocalDateTime createdAt;

}
