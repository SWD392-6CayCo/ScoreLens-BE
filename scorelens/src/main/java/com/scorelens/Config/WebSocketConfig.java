package com.scorelens.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
//Spring Boot WebSocket STOMP => server web socket riêng sử dụng STOMP protocol(Simple Text Oriented Messaging Protocol)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket endpoint with SockJS fallback (for web browsers)
        registry.addEndpoint("/ws")
                .setAllowedOrigins(
                    "http://localhost:3000",
                    "http://localhost:5173",
                    "https://localhost:5173",
                    "https://score-lens.vercel.app",
                    "exp://192.168.90.68:8081",
                    "https://scorelens.onrender.com"
                )
                .withSockJS();

        // Native WebSocket endpoint (for mobile apps)
        registry.addEndpoint("/ws")
                .setAllowedOrigins(
                    "http://localhost:3000",
                    "http://localhost:5173",
                    "https://localhost:5173",
                    "https://score-lens.vercel.app",
                    "exp://192.168.90.68:8081",
                    "https://scorelens.onrender.com"
                );
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
