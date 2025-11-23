/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coeus.backend_service.service;

/**
 *
 * @author ADMIN
 */
import com.coeus.backend_service.model.GameSession;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service dedicated to sending real-time updates to WebSocket clients using STOMP.
 */
@Service
public class GameMessagingService {

    private final SimpMessagingTemplate messagingTemplate;

    public GameMessagingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    
    // --- Public Subscription Destinations ---
    
    // Players subscribe here to get game state, question, and score updates
    private static final String GAME_UPDATES_DESTINATION = "/topic/rooms/%s/updates";

    // --- Sending Methods ---

    /**
     * Sends the full current GameSession state to all subscribers of a room.
     * @param session The GameSession object to send.
     */
    public void sendGameUpdate(GameSession session) {
        String destination = String.format(GAME_UPDATES_DESTINATION, session.getRoomCode());
        // In a real application, you would map this to a DTO to hide the correct answers.
        messagingTemplate.convertAndSend(destination, session); 
        
        System.out.println("WebSocket: Sent game update to " + destination + " (Status: " + session.getStatus() + ")");
    }

    /**
     * Sends a simple message to all subscribers of a room when a player joins or leaves.
     * @param roomCode The room code.
     * @param message The map of data to send (e.g., {"eventType": "PLAYER_JOINED", "userId": "..."}).
     */
    public void sendLobbyUpdate(String roomCode, Map<String, Object> message) {
        String destination = String.format(GAME_UPDATES_DESTINATION, roomCode);
        messagingTemplate.convertAndSend(destination, message);
    }
}
