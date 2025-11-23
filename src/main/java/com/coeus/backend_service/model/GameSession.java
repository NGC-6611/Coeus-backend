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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.coeus.backend_service.model.PlayerStatus.AnswerRecord; // Import the inner class

/**
 * Represents an active instance of a trivia game (the actual quiz session).
 * Created when a GameRoom transitions from LOBBY to RUNNING.
 */
@Entity
public class GameSession {

    // Using the same roomCode as the ID to easily link it to the GameRoom
    @Id
    private String roomCode;

    @ElementCollection(fetch = FetchType.EAGER)
    // Stores the ordered list of Question IDs for this specific session
    private List<Long> questionIds; 

    // Tracks which question is currently being presented to players
    private int currentQuestionIndex = -1; // -1 means game has not started yet

    private long questionStartTime; // Timestamp of when the current question was displayed

    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.WAITING; 
    
    // Key: userId (String)
    // Value: PlayerStatus (The embedded object we just created)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "session_player_statuses", joinColumns = @JoinColumn(name = "room_code"))
    @MapKeyColumn(name = "user_id") // Column for the Map Key (the userId)
    private Map<String, PlayerStatus> playerStatuses = new HashMap<>();

    // Enum to clearly define the state of the game
    public enum GameStatus {
        WAITING, // Game room created, waiting for players
        RUNNING, // Quiz is currently active
        FINISHED // Quiz is complete, displaying final scores
    }

    // --- Constructors ---
    public GameSession() {
    }
    
    /**
     * Constructor used when starting a new session from a GameRoom.
     * @param roomCode The code of the room.
     * @param playerIds The list of users starting the game.
     * @param questionIds The list of questions to use in order.
     */
    public GameSession(String roomCode, List<String> playerIds, List<Long> questionIds) {
        this.roomCode = roomCode;
        this.questionIds = questionIds;
        this.status = GameStatus.WAITING; // Start as WAITING until the first question is sent

        // Initialize PlayerStatus object for every player
        for (String userId : playerIds) {
            this.playerStatuses.put(userId, new PlayerStatus());
        }
    }
    
    // --- Business Logic ---
    
    public Long getCurrentQuestionId() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questionIds.size()) {
            return questionIds.get(currentQuestionIndex);
        }
        return null;
    }
    
    public boolean moveToNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questionIds.size()) {
            this.questionStartTime = System.currentTimeMillis();
            this.status = GameStatus.RUNNING;
            return true; // Moved to next question
        } else {
            this.status = GameStatus.FINISHED;
            return false; // Game finished
        }
    }

    // --- Getters and Setters ---
    
    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public List<Long> getQuestionIds() { return questionIds; }
    public void setQuestionIds(List<Long> questionIds) { this.questionIds = questionIds; }

    public int getCurrentQuestionIndex() { return currentQuestionIndex; }
    public void setCurrentQuestionIndex(int currentQuestionIndex) { this.currentQuestionIndex = currentQuestionIndex; }

    public long getQuestionStartTime() { return questionStartTime; }
    public void setQuestionStartTime(long questionStartTime) { this.questionStartTime = questionStartTime; }

    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }

    public Map<String, PlayerStatus> getPlayerStatuses() { return playerStatuses; }
    public void setPlayerStatuses(Map<String, PlayerStatus> playerStatuses) { this.playerStatuses = playerStatuses; }
}
