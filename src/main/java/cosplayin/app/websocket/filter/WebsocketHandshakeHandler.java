package cosplayin.app.websocket.filter;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Component
public class WebsocketHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected @Nullable Principal determineUser(@NonNull ServerHttpRequest request,
                                                @NonNull WebSocketHandler wsHandler,
                                                @NonNull Map<String, Object> attributes) {

        Object idObj = attributes.get("user_id");

        if (idObj instanceof UUID userId) {
            return userId::toString;
        }

        return null;

    }
}
