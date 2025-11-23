/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coeus.backend_service.controller;

/**
 *
 * @author ADMIN
 */
import com.coeus.backend_service.model.User;
import com.coeus.backend_service.service.UserService; // <-- Import the Service Layer
import org.springframework.http.HttpStatus; // <-- Import for status codes
import org.springframework.http.ResponseEntity; // <-- Import for flexible responses
import org.springframework.web.bind.annotation.*;

import java.util.Optional; // <-- Used by the Service result

/**
 * REST Controller for User management.
 * Handles HTTP requests for registration and login from the client app.
 */
@RestController // <--- Makes this a Web Service (Week 11)
@RequestMapping("/api/users") // Base URL: http://localhost:8080/api/users
public class UserController {

    // 1. DEPENDENCY: Inject the UserService, not the Repository
    private final UserService userService; 

    // Constructor Injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint for user registration.
     * POST http://localhost:8080/api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User newUser) {
        // Delegate business logic to the UserService
        User registeredUser = userService.registerUser(newUser);

        if (registeredUser == null) {
            // 409 Conflict if username already exists
            return new ResponseEntity<>(HttpStatus.CONFLICT); 
        }
        // 201 Created on success
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED); 
    }

    /**
     * Endpoint for user login (authentication).
     * POST http://localhost:8080/api/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody User loginAttempt) {
        // Delegate authentication logic to the UserService
        Optional<User> authenticatedUser = userService.authenticate(
                loginAttempt.getUsername(),
                loginAttempt.getPassword()
        );

        if (authenticatedUser.isPresent()) {
            // 200 OK and return the authenticated User data
            return new ResponseEntity<>(authenticatedUser.get(), HttpStatus.OK); 
        } else {
            // 401 Unauthorized if credentials are bad
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); 
        }
    }
}
