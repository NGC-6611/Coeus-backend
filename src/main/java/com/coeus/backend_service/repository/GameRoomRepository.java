/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coeus.backend_service.repository;

/**
 *
 * @author ADMIN
 */
import com.coeus.backend_service.model.GameRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for GameRoom entity. Provides standard CRUD methods and custom finders.
 */
@Repository
public interface GameRoomRepository extends JpaRepository<GameRoom, String> {

    // Custom finder to allow searching by the unique roomCode
    Optional<GameRoom> findByRoomCode(String roomCode);
    
    // NEW: Custom finder to list all rooms that have not started yet (Lobby view)
    List<GameRoom> findByGameStartedFalse();
}