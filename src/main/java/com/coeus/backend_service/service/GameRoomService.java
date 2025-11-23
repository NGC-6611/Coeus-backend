/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coeus.backend_service.service;

/**
 *
 * @author ADMIN
 */
import com.coeus.backend_service.model.GameRoom;
import com.coeus.backend_service.model.GameSession;
import com.coeus.backend_service.repository.GameRoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for handling game room creation and joining logic, and starting the session.
 */
@Service
public class GameRoomService {

    private final GameRoomRepository roomRepository;
    private final GameSessionService sessionService; // Inject the new service

    // Constructor Injection
    public GameRoomService(GameRoomRepository roomRepository, GameSessionService sessionService) {
        this.roomRepository = roomRepository;
        this.sessionService = sessionService;
    }

    /**
     * Creates a new GameRoom and automatically adds the creator as the first player.
     * @param creatorUserId The ID of the user creating the room.
     * @return The newly created GameRoom.
     */
    @Transactional
    public GameRoom createRoom(String creatorUserId) {
        GameRoom newRoom = new GameRoom(creatorUserId);
        return roomRepository.save(newRoom);
    }

    /**
     * Attempts to find an existing room and add a player to it.
     * @param roomCode The unique code of the room to join.
     * @param userId The ID of the user attempting to join.
     * @return An Optional containing the updated GameRoom on success, or empty if join fails.
     */
    @Transactional
    public Optional<GameRoom> joinRoom(String roomCode, String userId) {
        // Find the room by its unique code (case-insensitive find)
        Optional<GameRoom> roomOptional = roomRepository.findByRoomCode(roomCode.toUpperCase());

        if (roomOptional.isPresent()) {
            GameRoom room = roomOptional.get();
            
            // Check if room is joinable (not started and not full)
            if (room.addPlayer(userId)) {
                // Save the updated room with the new player
                return Optional.of(roomRepository.save(room));
            }
        }
        // Room not found, game already started, or room is full
        return Optional.empty();
    }

    /**
     * Starts the game for the specified room.
     * This transitions the GameRoom status and creates a GameSession.
     * @param roomCode The code of the room to start.
     * @param creatorUserId The ID of the user trying to start the game (must be the creator).
     * @return The new GameSession object if successful.
     */
    @Transactional
    public Optional<GameSession> startGame(String roomCode, String creatorUserId) {
        Optional<GameRoom> roomOptional = roomRepository.findByRoomCode(roomCode.toUpperCase());

        if (roomOptional.isPresent()) {
            GameRoom room = roomOptional.get();

            // 1. Validation: Only the creator can start the game
            if (!room.getCreatorUserId().equals(creatorUserId)) {
                // You could throw an Unauthorized exception here, but for now, return empty
                return Optional.empty(); 
            }
            
            // 2. Validation: Ensure minimum players (e.g., 2)
            if (room.getCurrentPlayers().size() < 2) {
                // Not enough players
                return Optional.empty();
            }

            // 3. Mark the GameRoom as started (so no one else can join)
            room.setGameStarted(true);
            roomRepository.save(room);

            // 4. Create the GameSession, which contains the quiz logic
            GameSession session = sessionService.createAndStartSession(room);
            return Optional.of(session);
        }
        return Optional.empty();
    }

    /**
     * Gets a list of all active (not started) game rooms.
     */
    public List<GameRoom> getAllRooms() {
        // Filters rooms that haven't started yet
        return roomRepository.findByGameStartedFalse();
    }
}