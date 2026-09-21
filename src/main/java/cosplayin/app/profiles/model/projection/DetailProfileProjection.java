package cosplayin.app.profiles.model.projection;

import java.util.UUID;

import cosplayin.app.core.authorization.UserRoles;
import cosplayin.app.core.authorization.UserStatus;
import cosplayin.app.profiles.model.type.ProfilesVisibility;

public interface DetailProfileProjection {
    UUID getId();

    String getDisplayName();

    String getBio();

    String getAvatarUrl();

    String getBannerUrl();

    String getCosplayTags();

    ProfilesVisibility getVisibility();

    UserProjection getUser();

    interface UserProjection {
        String getUsername();

        UserStatus getStatus();

        UserRoles getRole();
    }
}