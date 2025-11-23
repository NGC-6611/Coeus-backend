/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coeus.backend_service.controller;

/**
 *
 * @author ADMIN
 */
import com.coeus.backend_service.model.GameSession;
import com.coeus.backend_service.service.GameSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * REST Controller for managing active trivia game sessions.
 */
@RestController
@RequestMapping("/api/game") // Base URL: http://localhost:8080/api/game
public class GameSessionController {

    private final GameSessionService sessionService;

    public GameSessionController(GameSessionService sessionService) {
        this.sessionService = sessionService;
    }
    
    /**
     * Endpoint to retrieve the current state of an active game session,
     * including current question index and live scores.
     * GET http://localhost:8080/api/game/{roomCode}/status
     */
    @GetMapping("/{roomCode}/status")
    public ResponseEntity<GameSession> getSessionStatus(@PathVariable String roomCode) {
        Optional<GameSession> session = sessionService.getSession(roomCode);
        return session.map(s -> new ResponseEntity<>(s, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Endpoint for a player to submit an answer.
     * The timeTakenMs is calculated on the client side based on questionStartTime.
     * POST http://localhost:8080/api/game/answer
     */
    @PostMapping("/answer")
    public ResponseEntity<Void> submitAnswer(@RequestBody GameAnswerRequest request) {
        // Validate time taken to prevent cheating (e.g., if time is negative or too long)
        if (request.getTimeTakenMs() < 0 || request.getTimeTakenMs() > 10000) { // Max 10 seconds allowed
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        
        boolean success = sessionService.submitAnswer(
                request.getRoomCode(),
                request.getUserId(),
                request.getQuestionId(),
                request.getSubmittedAnswer(),
                request.getTimeTakenMs()
        );

        if (success) {
            return new ResponseEntity<>(HttpStatus.OK); // 200 OK
        } else {
            // Either session not found, user not in session, or question already answered
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
        }
    }

    /**
     * Endpoint to move the game to the next question.
     * This should ideally only be called by the room creator or server-side timer.
     * POST http://localhost:8080/api/game/{roomCode}/next
     */
    @PostMapping("/{roomCode}/next")
    public ResponseEntity<GameSession> nextQuestion(@PathVariable String roomCode) {
        // NOTE: A robust implementation would verify the user making this request is the creator.
        Optional<GameSession> updatedSession = sessionService.nextQuestion(roomCode);

        if (updatedSession.isPresent()) {
            return new ResponseEntity<>(updatedSession.get(), HttpStatus.OK);
        } else {
            // Game is finished or room not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
        }
    }
}
