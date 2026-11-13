package cosplayin.app.conversation.model.dto;

import cosplayin.app.conversation.model.type.ConversationRoles;
import cosplayin.app.profiles.model.dto.SimpleProfileDTO;
import lombok.*;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ParticipantResponse {
    private UUID id;
    private SimpleProfileDTO user;
    private ConversationRoles role;
}
