package cosplayin.app.websocket.filter;


import cosplayin.app.session.model.SessionAuthContext;
import cosplayin.app.session.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebsocketHandshakeFilter implements HandshakeInterceptor {

    private final SessionService sessionService;

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request,
                                   @NonNull ServerHttpResponse response,
                                   @NonNull WebSocketHandler wsHandler,
                                   @NonNull Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest =
                    servletRequest.getServletRequest();


            String accessToken = null;

            Cookie[] cookies = httpRequest.getCookies();

            if (cookies != null) {
                for (Cookie c : cookies) {
                    if ("access_token".equals(c.getName())) {
                        accessToken = c.getValue();

                    }
                }
            }

            if (accessToken != null && !accessToken.trim().isEmpty()) {
                SessionAuthContext ctx = sessionService.findSessionAndCreds(accessToken);

                if (ctx != null) {
                    if (ctx.credentials() != null) {
                        attributes.put("user_id", ctx.sessionData().getId());
                        return true;
                    } else {
                        return false;
                    }
                }

            }

        }

        return false;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, @Nullable Exception exception) {

    }
}
