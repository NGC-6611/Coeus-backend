/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coeus.backend_service.model;

/**
 *
 * @author ADMIN
 */

import com.coeus.backend_service.converter.AnswerRecordListConverter; // NEW IMPORT
import jakarta.persistence.Convert; // NEW IMPORT
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Tracks the status and score of a single player within an active GameSession.
 * Marked as @Embeddable so it can be stored directly within the GameSession entity.
 */
@Embeddable
public class PlayerStatus implements Serializable {

    // IMPORTANT: userId is the key in the map in GameSession, so we don't store it here.
    
    private int score = 0;
    private int questionsAnswered = 0;
    private int correctAnswers = 0;

    // FIX: Replaced @ElementCollection with @Convert. This will store the List<AnswerRecord>
    // as a single JSON string in the database column, bypassing the nested collection error.
    @Convert(converter = AnswerRecordListConverter.class)
    private List<AnswerRecord> answers = new ArrayList<>(); 

    // --- Constructors ---
    public PlayerStatus() {
    }

    // --- Business Logic ---

    /**
     * Updates the player's score and stats based on an answer result.
     * @param isCorrect True if the answer was correct.
     * @param timeTakenMs The time taken to answer, in milliseconds.
     * @param questionId The ID of the question answered.
     */
    public void recordAnswer(boolean isCorrect, long timeTakenMs, Long questionId) {
        questionsAnswered++;
        answers.add(new AnswerRecord(questionId, isCorrect, timeTakenMs));

        if (isCorrect) {
            correctAnswers++;
            // Scoring logic: Base score (e.g., 100 points) + speed bonus
            int baseScore = 100;
            // Simple speed bonus: faster is better. 10s max time means max bonus is 100
            long maxTimeMs = 10000; 
            long timeBonus = Math.max(0, maxTimeMs - timeTakenMs) / 100; // Divide by 100 to scale
            
            this.score += baseScore + timeBonus;
        }
    }
    
    // --- Getters and Setters ---
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getQuestionsAnswered() { return questionsAnswered; }
    public void setQuestionsAnswered(int questionsAnswered) { this.questionsAnswered = questionsAnswered; }

    public int getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(int correctAnswers) { this.correctAnswers = correctAnswers; }

    public List<AnswerRecord> getAnswers() { return answers; }
    public void setAnswers(List<AnswerRecord> answers) { this.answers = answers; }

    /**
     * Helper class to store the record of a single answer submission.
     */
    @Embeddable
    public static class AnswerRecord {
        private Long questionId;
        private boolean isCorrect;
        private long timeTakenMs;

        public AnswerRecord() {
        }

        public AnswerRecord(Long questionId, boolean isCorrect, long timeTakenMs) {
            this.questionId = questionId;
            this.isCorrect = isCorrect;
            this.timeTakenMs = timeTakenMs;
        }
        
        // Getters and Setters for AnswerRecord
        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }

        public boolean isCorrect() { return isCorrect; }
        public void setCorrect(boolean correct) { isCorrect = correct; }

        public long getTimeTakenMs() { return timeTakenMs; }
        public void setTimeTakenMs(long timeTakenMs) { this.timeTakenMs = timeTakenMs; }
    }
}