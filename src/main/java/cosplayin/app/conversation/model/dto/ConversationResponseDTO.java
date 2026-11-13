package cosplayin.app.conversation.model.dto;

import cosplayin.app.conversation.model.type.ConversationType;
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
public class ConversationResponseDTO {
    private UUID id;
    private String conversationImage;
    private String name;
    private String convKey;
    private LocalDateTime createdAt;
    private ConversationType conversationType;
    private List<ParticipantResponse> participants;
    private List<ConversationLatestMessageResponse> latestMessage;
}
