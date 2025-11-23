/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coeus.backend_service.config;

/**
 *
 * @author ADMIN
 */
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class to enable and set up Spring's STOMP WebSockets.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Registers the STOMP endpoint for clients to connect to.
     * Clients will connect via ws://localhost:8080/ws
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // We expose a single WebSocket endpoint '/ws'
        // .setAllowedOrigins("*") is used for local development to allow any client access.
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
        
        // This is the primary WebSocket endpoint without SockJS fallback
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
    }

    /**
     * Configures the message broker.
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 1. Destination for application-specific messages (controller routing)
        // Messages sent to /app/... will be routed to @MessageMapping annotated methods.
        registry.setApplicationDestinationPrefixes("/app");
        
        // 2. Destination for messages being sent back to the client (subscriptions)
        // Clients subscribe to /topic/rooms/{roomCode}/updates for real-time data.
        registry.enableSimpleBroker("/topic");
    }
}
