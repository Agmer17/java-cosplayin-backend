package cosplayin.app.notification.model.dto;

import lombok.Builder;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.UUID;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ActorNotificationPreview(
        UUID userId,
        String username,
        String profilePicture
) {
}
