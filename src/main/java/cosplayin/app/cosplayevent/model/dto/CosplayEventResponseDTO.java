package cosplayin.app.cosplayevent.model.dto;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CosplayEventResponseDTO(
        UUID id,
        String title,
        String description,
        String mediaUrl,
        LocalDateTime startAt,
        LocalDateTime endAt,
        String eventInformationUrl,
        CosplayEventLocationResponseDTO location,
        LocalDateTime createdAt
) {
}
