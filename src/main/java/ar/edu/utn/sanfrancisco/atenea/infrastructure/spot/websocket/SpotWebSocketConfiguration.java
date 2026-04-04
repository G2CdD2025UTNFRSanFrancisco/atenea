package ar.edu.utn.sanfrancisco.atenea.infrastructure.spot.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class SpotWebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final String endpoint;
    private final String topicPrefix;
    private final String appPrefix;

    public SpotWebSocketConfiguration(
            @Value("${atenea.websocket.endpoint:/ws}") final String endpoint,
            @Value("${atenea.websocket.topic-prefix:/topic}") final String topicPrefix,
            @Value("${atenea.websocket.app-prefix:/app}") final String appPrefix
    ) {
        this.endpoint = endpoint;
        this.topicPrefix = topicPrefix;
        this.appPrefix = appPrefix;
    }

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint(this.endpoint).setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(final MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(this.topicPrefix);
        registry.setApplicationDestinationPrefixes(this.appPrefix);
    }
}

