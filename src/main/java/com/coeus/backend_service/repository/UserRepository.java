/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coeus.backend_service.repository;

/**
 *
 * @author ADMIN
 */
import com.coeus.backend_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// This automates CRUD (Save, Delete, Find) without writing code
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // Custom finder (Spring generates the SQL automatically)
    Optional<User> findByUsername(String username); 
}
