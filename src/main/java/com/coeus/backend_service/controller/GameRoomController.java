/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coeus.backend_service.controller;

/**
 *
 * @author ADMIN
 */
import com.coeus.backend_service.model.GameRoom;
import com.coeus.backend_service.service.GameRoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for GameRoom management (creating and joining).
 */
@RestController
@RequestMapping("/api/rooms") // Base URL: http://localhost:8080/api/rooms
public class GameRoomController {

    private final GameRoomService roomService;

    public GameRoomController(GameRoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * Endpoint to create a new game room.
     * Requires the creator's user ID.
     * POST http://localhost:8080/api/rooms/create
     */
    @PostMapping("/create/{userId}")
    public ResponseEntity<GameRoom> createRoom(@PathVariable String userId) {
        GameRoom newRoom = roomService.createRoom(userId);
        return new ResponseEntity<>(newRoom, HttpStatus.CREATED); // 201 Created
    }

    /**
     * Endpoint to join an existing game room.
     * Requires the room code and the joining user's ID.
     * POST http://localhost:8080/api/rooms/join
     * Request Body: {"roomCode": "XXXXXX", "userId": "user-id-here"}
     */
    @PostMapping("/join")
    public ResponseEntity<GameRoom> joinRoom(@RequestBody GameRoomJoinRequest request) {
        Optional<GameRoom> updatedRoom = roomService.joinRoom(request.getRoomCode(), request.getUserId());

        if (updatedRoom.isPresent()) {
            return new ResponseEntity<>(updatedRoom.get(), HttpStatus.OK); // 200 OK
        } else {
            // Can be 404 Not Found, or 403 Forbidden (if room is full/started)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
        }
    }

    /**
     * Endpoint to list all active rooms (for the lobby view).
     * GET http://localhost:8080/api/rooms
     */
    @GetMapping
    public ResponseEntity<List<GameRoom>> getAllRooms() {
        List<GameRoom> rooms = roomService.getAllRooms();
        return new ResponseEntity<>(rooms, HttpStatus.OK); // 200 OK
    }
    
    // Simple DTO (Data Transfer Object) for the join request body
    // This is defined inside the controller for simplicity but could be a separate class
    public static class GameRoomJoinRequest {
        private String roomCode;
        private String userId;

        public String getRoomCode() { return roomCode; }
        public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
    }
}
