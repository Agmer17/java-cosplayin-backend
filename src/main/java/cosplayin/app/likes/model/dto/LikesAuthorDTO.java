package cosplayin.app.likes.model.dto;

import java.util.UUID;

import cosplayin.app.core.authorization.UserRoles;
import lombok.Builder;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record LikesAuthorDTO(
        UUID userId,
        String username,
        String displayName,
        UserRoles role,
        String avatarUrl) {

}
