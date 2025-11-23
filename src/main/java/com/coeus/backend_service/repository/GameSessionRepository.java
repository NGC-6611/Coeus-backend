/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coeus.backend_service.repository;

/**
 *
 * @author ADMIN
 */
import com.coeus.backend_service.model.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for GameSession entity.
 * Uses the roomCode as the primary key (String).
 */
@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, String> {

    /**
     * Find an active game session by its room code.
     */
    Optional<GameSession> findByRoomCode(String roomCode);
}
