package cosplayin.app.session.model;

import cosplayin.app.security.context.UserCredentials;

public record SessionAuthContext(
        SessionDataModel sessionData,
        UserCredentials credentials) {

}
