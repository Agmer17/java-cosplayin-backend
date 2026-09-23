package cosplayin.app.core.event;

import cosplayin.app.security.context.UserCredentials;

public record UsersCredentialUpdateEvent(
        UserCredentials newCredentials) {

}
