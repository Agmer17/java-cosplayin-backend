package cosplayin.app.cosplayevent.model.dto;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CosplayEventLocationResponseDTO(
        UUID id,
        String name,
        String address,
        String locationType,
        Long osmId,
        String osmType,
        Double latitude,
        Double longitude
) {
}
