package cosplayin.app.websocket.filter;

import cosplayin.app.core.exception.model.UnauthorizedAccessExceptions;
import cosplayin.app.utils.GetOptionalCurrentUsers;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.UUID;

@Component
public class WebsocketHandshakeFilter implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest =
                    servletRequest.getServletRequest();

            UUID currentUserId = GetOptionalCurrentUsers.getViewerId(httpRequest);
            if (currentUserId == null) throw new UnauthorizedAccessExceptions("you need to login to access the webscket");

            attributes.put("user_id", currentUserId);
            return true;

        } else {
            return false;
        }
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @Nullable Exception exception) {

    }
}
