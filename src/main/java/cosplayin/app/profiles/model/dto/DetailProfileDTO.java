package cosplayin.app.profiles.model.dto;

import java.util.UUID;

import cosplayin.app.core.authorization.UserRoles;
import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.profiles.model.type.ProfilesVisibility;
import lombok.Builder;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DetailProfileDTO(
        UUID id,
        String displayName,
        String bio,
        String avatarUrl,
        String bannerUrl,
        ProfilesVisibility visibility,
        String username,
        UserStatus userStatus,
        UserRoles userRole) {
}
