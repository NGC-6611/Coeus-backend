/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coeus.backend_service.model;

/**
 *
 * @author ADMIN
 */
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a single game room where a trivia game is hosted.
 * Tracks the room code, creator, and current players.
 */
@Entity
public class GameRoom {

    @Id
    private String roomCode; // Using a short generated code as the ID

    private String creatorUserId; 
    
    // Players are tracked by their User ID (String)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "room_players", joinColumns = @JoinColumn(name = "room_code"))
    @Column(name = "user_id")
    private Set<String> currentPlayers = new HashSet<>();

    private long creationTimestamp;
    private boolean gameStarted = false;
    private int maxPlayers = 4; // Default limit

    // --- Constructor ---
    public GameRoom() {
        // Generate a 6-character UUID prefix for a simple room code
        this.roomCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.creationTimestamp = System.currentTimeMillis();
    }
    
    // Constructor used when a specific creator is known
    public GameRoom(String creatorUserId) {
        this();
        this.creatorUserId = creatorUserId;
        this.currentPlayers.add(creatorUserId); // Creator joins automatically
    }

    // --- Business Logic Helper ---
    public boolean addPlayer(String userId) {
        if (!gameStarted && currentPlayers.size() < maxPlayers) {
            return currentPlayers.add(userId);
        }
        return false;
    }

    // --- Getters and Setters ---
    public String getRoomCode() { return roomCode; }
    // Setter for roomCode is generally not needed as it's generated, but included for JPA
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; } 

    public String getCreatorUserId() { return creatorUserId; }
    public void setCreatorUserId(String creatorUserId) { this.creatorUserId = creatorUserId; }

    public Set<String> getCurrentPlayers() { return currentPlayers; }
    public void setCurrentPlayers(Set<String> currentPlayers) { this.currentPlayers = currentPlayers; }

    public long getCreationTimestamp() { return creationTimestamp; }
    public void setCreationTimestamp(long creationTimestamp) { this.creationTimestamp = creationTimestamp; }

    public boolean isGameStarted() { return gameStarted; }
    public void setGameStarted(boolean gameStarted) { this.gameStarted = gameStarted; }

    public int getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers = maxPlayers; }
}
