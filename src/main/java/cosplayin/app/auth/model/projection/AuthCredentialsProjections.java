package cosplayin.app.auth.model.projection;

import java.util.UUID;

import cosplayin.app.auth.model.type.AuthProvider;
import cosplayin.app.core.authorization.UserRoles;
import cosplayin.app.core.authorization.UserStatus;

public interface AuthCredentialsProjections {
    UUID getUserId();

    AuthProvider getProvider();

    UserStatus getStatus();

    UserRoles getUserRoles();
}
