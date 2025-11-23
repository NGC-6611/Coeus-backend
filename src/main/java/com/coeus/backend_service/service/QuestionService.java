/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coeus.backend_service.service;

/**
 *
 * @author ADMIN
 */
import com.coeus.backend_service.model.Question;
import com.coeus.backend_service.repository.QuestionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to load initial trivia questions into the database upon application startup.
 * This is used for development/testing purposes while using H2 (create-drop).
 */
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    /**
     * Executes immediately after the bean is constructed.
     * Checks if the database is empty and, if so, loads initial questions.
     */
    @PostConstruct
    public void loadInitialQuestions() {
        // Only load if the database is empty
        if (questionRepository.count() == 0) {
            List<Question> sampleQuestions = List.of(
                new Question(
                    "What is the capital of Australia?",
                    "Sydney", "Melbourne", "Canberra", "Brisbane",
                    "Canberra", "Geography", 2
                ),
                new Question(
                    "Which planet is known as the Red Planet?",
                    "Jupiter", "Mars", "Venus", "Mercury",
                    "Mars", "Science", 1
                ),
                new Question(
                    "In computing, what does 'HTTP' stand for?",
                    "HyperText Transfer Protocol", "High-Tech Task Processor", "Hyperlink Text Processor", "Home Taping Time Protocol",
                    "HyperText Transfer Protocol", "Technology", 3
                ),
                new Question(
                    "Who wrote 'To Kill a Mockingbird'?",
                    "Harper Lee", "Mark Twain", "F. Scott Fitzgerald", "Ernest Hemingway",
                    "Harper Lee", "Literature", 2
                ),
                new Question(
                    "What chemical element has the symbol 'Fe'?",
                    "Fluorine", "Iron", "Ferrum", "Silver",
                    "Iron", "Science", 1
                )
            );
            
            questionRepository.saveAll(sampleQuestions);
            System.out.println("--- 5 Sample Trivia Questions Loaded Successfully ---");
        }
    }
}
