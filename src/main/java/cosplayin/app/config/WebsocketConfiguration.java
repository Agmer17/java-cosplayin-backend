package cosplayin.app.config;

import cosplayin.app.websocket.filter.WebsocketHandshakeFilter;
import cosplayin.app.websocket.filter.WebsocketHandshakeHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final WebsocketHandshakeFilter handshakeFilterInterceptor;
    private final WebsocketHandshakeHandler customPrincipalHandler;
    private final TaskScheduler scheduler;

    public WebsocketConfiguration(
            WebsocketHandshakeFilter handshakeFilterInterceptor,
            WebsocketHandshakeHandler customPrincipalHandler,
            @Qualifier("websocketHeartbeatScheduler") TaskScheduler scheduler) {
        this.handshakeFilterInterceptor = handshakeFilterInterceptor;
        this.customPrincipalHandler = customPrincipalHandler;
        this.scheduler = scheduler;
    }


   @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/api/ws")
            .setAllowedOriginPatterns("http://localhost:3000",
                        "http://192.168.1.12:3000")
            .addInterceptors(handshakeFilterInterceptor)

            .setHandshakeHandler(customPrincipalHandler);
}

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/broadcast", "/personal")
                .setHeartbeatValue(new long[]{10000, 10000})
                .setTaskScheduler(scheduler);
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

}
