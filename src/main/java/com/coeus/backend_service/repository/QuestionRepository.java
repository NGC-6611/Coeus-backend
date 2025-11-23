/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coeus.backend_service.repository;

/**
 *
 * @author ADMIN
 */
import com.coeus.backend_service.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Question entity.
 * Provides methods for CRUD operations and custom queries for trivia selection.
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * Finds a list of questions filtered by a specific category.
     */
    List<Question> findByCategory(String category);
    
    /**
     * Finds a list of questions filtered by a specific difficulty level.
     */
    List<Question> findByDifficultyLevel(int difficultyLevel);
}
