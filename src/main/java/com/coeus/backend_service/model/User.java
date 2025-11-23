/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.coeus.backend_service.model;

/**
 *
 * @author ADMIN
 */
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;

/**
 * Represents a User/Player in the system. Mapped to the 'users' table by JPA.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) 
    private String id;

    private String username;
    private String password; 
    // This field is used in UserService to track the last successful login time.
    private long lastLoginTimestamp; 

    // --- Constructors (Required for JPA) ---
    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.lastLoginTimestamp = System.currentTimeMillis();
    }

    // --- Getters and Setters (CRITICAL: Must return the field values) ---
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUsername() { 
        return username; // <--- FIX: RETURN THE FIELD
    }
    public void setUsername(String username) {
        this.username = username; 
    }
    
    public String getPassword() { 
        return password; // <--- FIX: RETURN THE FIELD
    }
    public void setPassword(String password) { 
        this.password = password; 
    }
    
    public long getLastLoginTimestamp() { 
        return lastLoginTimestamp; // <--- FIX: RETURN THE FIELD
    }
    public void setLastLoginTimestamp(long lastLoginTimestamp) { 
        this.lastLoginTimestamp = lastLoginTimestamp; 
    }
}