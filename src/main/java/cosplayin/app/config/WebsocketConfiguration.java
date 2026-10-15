package cosplayin.app.config;

import cosplayin.app.websocket.filter.WebsocketHandshakeFilter;
import cosplayin.app.websocket.filter.WebsocketHandshakeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final WebsocketHandshakeFilter handshakeFilterInterceptor;
    private final WebsocketHandshakeHandler customPrincipalHandler;
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                    .setAllowedOriginPatterns("http://localhost:3000", "http://192.168.1.8:3000")
                    .addInterceptors(handshakeFilterInterceptor)
                    .setHandshakeHandler(customPrincipalHandler);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/broadcast", "/inbox");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
}
