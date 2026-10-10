package cosplayin.app.profiles.model.dto;

import java.util.UUID;

import cosplayin.app.core.authorization.UserRoles;
import cosplayin.app.profiles.model.type.ProfilesVisibility;
import lombok.Builder;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SimpleProfileDTO(
        UUID id,
        String displayName,
        String avatarUrl,
        ProfilesVisibility visibility,
        String username,
        UserRoles userRole) {

}
