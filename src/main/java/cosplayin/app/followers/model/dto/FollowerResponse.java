package cosplayin.app.followers.model.dto;

import java.time.LocalDateTime;

import cosplayin.app.followers.model.type.FollowStatus;
import cosplayin.app.profiles.model.dto.SimpleProfileDTO;
import lombok.Builder;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record FollowerResponse(
                SimpleProfileDTO user,
                FollowStatus status,
                LocalDateTime createdAt) {
}
