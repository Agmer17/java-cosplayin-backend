package cosplayin.app.utils;

import java.util.UUID;

import cosplayin.app.session.model.SessionDataModel;
import jakarta.servlet.http.HttpServletRequest;

public class GetOptionalCurrentUsers {
    public static UUID getViewerId(HttpServletRequest request) {
        SessionDataModel session = (SessionDataModel) request.getAttribute("session");

        return session != null ? session.getId() : null;
    }
}
