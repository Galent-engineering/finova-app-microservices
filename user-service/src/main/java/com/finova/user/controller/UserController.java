package com.finova.user.controller;

import com.finova.user.dto.UserProfileDTO;
import com.finova.user.dto.LoginRequest;
import com.finova.user.dto.LoginResponse;
import com.finova.user.dto.RegisterRequest;
import com.finova.user.model.User;
import com.finova.user.service.UserService;
import com.finova.user.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for User management operations
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthService authService;

    /**
     * User authentication endpoint
     */
    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.authenticate(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * User registration endpoint
     */
    @PostMapping("/auth/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        LoginResponse response = authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user profile by ID
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long id) {
        UserProfileDTO profile = userService.getUserProfile(id);
        return ResponseEntity.ok(profile);
    }

    /**
     * Get user profile by username
     */
    @GetMapping("/users/profile/{username}")
    public ResponseEntity<UserProfileDTO> getUserProfileByUsername(@PathVariable String username) {
        UserProfileDTO profile = userService.getUserProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }

    /**
     * Update user profile
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<UserProfileDTO> updateUserProfile(
            @PathVariable Long id, 
            @Valid @RequestBody UserProfileDTO profileDTO) {
        UserProfileDTO updatedProfile = userService.updateUserProfile(id, profileDTO);
        return ResponseEntity.ok(updatedProfile);
    }

    /**
     * Delete user account
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/users/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("User Service is running");
    }
}
