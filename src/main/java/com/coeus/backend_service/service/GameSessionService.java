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
import com.coeus.backend_service.model.Question;
import com.coeus.backend_service.model.PlayerStatus;
import com.coeus.backend_service.repository.GameSessionRepository;
import com.coeus.backend_service.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Orchestrates the active game session logic, including starting the game,
 * moving through questions, and handling player answers/scoring.
 * This service now pushes real-time updates via GameMessagingService.
 */
@Service
public class GameSessionService {

    private final GameSessionRepository sessionRepository;
    private final QuestionRepository questionRepository;
    private final GameMessagingService messagingService; // Messaging service injected
    // We will need GameRoomService later to transition the room status

    // Constructor Injection (Now includes GameMessagingService)
    public GameSessionService(GameSessionRepository sessionRepository, QuestionRepository questionRepository, GameMessagingService messagingService) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
        this.messagingService = messagingService;
    }

    /**
     * Creates and starts a new GameSession based on an existing GameRoom.
     * @param room The GameRoom to convert into a session.
     * @return The newly created GameSession.
     */
    @Transactional
    public GameSession createAndStartSession(GameRoom room) {
        // 1. Fetch questions (e.g., 10 random questions for simplicity)
        List<Question> allQuestions = questionRepository.findAll();
        // Simple selection: take the first 10, or fewer if not enough exist
        List<Question> sessionQuestions = allQuestions.stream()
                .limit(10)
                .collect(Collectors.toList());

        if (sessionQuestions.isEmpty()) {
            throw new IllegalStateException("Not enough questions available to start the game.");
        }
        
        List<Long> questionIds = sessionQuestions.stream()
                .map(Question::getId)
                .collect(Collectors.toList());

        // 2. Initialize GameSession
        GameSession newSession = new GameSession(
            room.getRoomCode(),
            List.copyOf(room.getCurrentPlayers()), // Pass player IDs
            questionIds
        );
        
        // 3. Move to the first question (sets the start time and status to RUNNING)
        newSession.moveToNextQuestion();
        
        // Save the session and get the saved instance
        GameSession savedSession = sessionRepository.save(newSession);
        
        // PUSH UPDATE: Notify all players the game has started and the first question is active
        messagingService.sendGameUpdate(savedSession);
        
        return savedSession; // Return the saved session
    }

    /**
     * Retrieves the current state of a game session.
     * @param roomCode The code of the session.
     * @return Optional containing the GameSession.
     */
    public Optional<GameSession> getSession(String roomCode) {
        return sessionRepository.findById(roomCode);
    }
    
    /**
     * Moves the session to the next question.
     * @param roomCode The code of the session.
     * @return The updated GameSession, or Optional.empty() if the game is finished or room not found.
     */
    @Transactional
    public Optional<GameSession> nextQuestion(String roomCode) {
        return sessionRepository.findById(roomCode).map(session -> {
            boolean hasNext = session.moveToNextQuestion();
            
            // Save the updated session state (new question index or FINISHED status)
            GameSession updatedSession = sessionRepository.save(session);
            
            // PUSH UPDATE: Notify all players about the new question or the end of the game
            messagingService.sendGameUpdate(updatedSession);

            if (!hasNext) {
                System.out.println("Game " + roomCode + " finished.");
            }
            return updatedSession;
        });
    }

    /**
     * Processes a player's answer submission, calculates score, and updates PlayerStatus.
     * @param roomCode The session code.
     * @param userId The ID of the player submitting the answer.
     * @param questionId The ID of the question being answered.
     * @param submittedAnswer The text of the option submitted by the user.
     * @param timeTakenMs The time elapsed since the question started (for speed bonus).
     * @return True if the answer was successfully recorded, false otherwise (e.g., session not found).
     */
    @Transactional
    public boolean submitAnswer(String roomCode, String userId, Long questionId, String submittedAnswer, long timeTakenMs) {
        Optional<GameSession> sessionOptional = sessionRepository.findById(roomCode);

        if (sessionOptional.isEmpty()) {
            return false; // Session not found
        }

        GameSession session = sessionOptional.get();
        PlayerStatus playerStatus = session.getPlayerStatuses().get(userId);

        // Basic validation: Check if the player is in the game and hasn't answered this question yet
        if (playerStatus == null || playerStatus.getAnswers().stream().anyMatch(a -> a.getQuestionId().equals(questionId))) {
            return false;
        }

        // 1. Get the correct question from the database
        Optional<Question> questionOptional = questionRepository.findById(questionId);
        if (questionOptional.isEmpty()) {
            return false; // Question not found (shouldn't happen in a real game)
        }
        Question question = questionOptional.get();

        // 2. Determine correctness
        boolean isCorrect = question.getCorrectAnswer().equalsIgnoreCase(submittedAnswer);

        // 3. Update player status
        playerStatus.recordAnswer(isCorrect, timeTakenMs, questionId);
        
        // 4. Save the updated session
        GameSession savedSession = sessionRepository.save(session);

        // 5. PUSH UPDATE: Notify all players about the score change and updated status
        // This makes the leaderboard update instantly across all connected clients.
        messagingService.sendGameUpdate(savedSession);
        
        return true;
    }
}