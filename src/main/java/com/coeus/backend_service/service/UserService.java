/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coeus.backend_service.service;

/**
 *
 * @author ADMIN
 */
import com.coeus.backend_service.model.User;
import com.coeus.backend_service.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional; // <--- MAKE SURE THIS IMPORT IS PRESENT

/**
 * Service layer for handling user-related business logic (registration, login).
 * It uses the UserRepository to interact with the database.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    // Dependency Injection via constructor
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Attempts to register a new user.
     * @param user The user object containing username and password.
     * @return The newly saved User object, or null if the username already exists.
     */
    public User registerUser(User user) {
        // Check if username already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return null; // Username conflict
        }
        
        // Save the new user
        user.setLastLoginTimestamp(System.currentTimeMillis());
        return userRepository.save(user);
    }

    /**
     * Authenticates a user based on username and password.
     * @param username The username provided.
     * @param password The password provided.
     * @return The authenticated User object, or an empty Optional if credentials are invalid.
     */
    public Optional<User> authenticate(String username, String password) {
        // 1. Fetch the user from the database by username
        Optional<User> userOptional = userRepository.findByUsername(username);

        // 2. Check if the user exists
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // 3. Compare passwords (this is where the logic happens)
            if (user.getPassword().equals(password)) {
                // Update login timestamp on successful login
                user.setLastLoginTimestamp(System.currentTimeMillis());
                userRepository.save(user);
                return Optional.of(user);
            }
        }
        // User not found or password incorrect
        return Optional.empty();
    }
}