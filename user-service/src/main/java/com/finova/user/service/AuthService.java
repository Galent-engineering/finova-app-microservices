package com.finova.user.service;

import com.finova.user.dto.LoginRequest;
import com.finova.user.dto.LoginResponse;
import com.finova.user.dto.RegisterRequest;
import com.finova.user.model.User;
import com.finova.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service class for Authentication operations
 */
@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    /**
     * Authenticate user
     */
    public LoginResponse authenticate(LoginRequest loginRequest) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
            
            if (userOptional.isEmpty()) {
                return new LoginResponse("Invalid username or password");
            }
            
            User user = userOptional.get();
            
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return new LoginResponse("Invalid username or password");
            }
            
            if (!user.isEnabled()) {
                return new LoginResponse("Account is disabled");
            }
            
            // Update last login
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
            
            // Generate JWT token
            String token = jwtService.generateToken(user.getUsername(), user.getId());
            
            return new LoginResponse(token, user.getId(), user.getUsername(), user.getEmail());
            
        } catch (Exception e) {
            return new LoginResponse("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Register new user
     */
    public LoginResponse register(RegisterRequest registerRequest) {
        try {
            // Check if username already exists
            if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
                return new LoginResponse("Username already exists");
            }
            
            // Check if email already exists
            if (registerRequest.getEmail() != null && 
                userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                return new LoginResponse("Email already exists");
            }
            
            // Create new user
            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setEmail(registerRequest.getEmail());
            user.setEnabled(true);
            user.setRole("USER");
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            
            User savedUser = userRepository.save(user);
            
            // Generate JWT token
            String token = jwtService.generateToken(savedUser.getUsername(), savedUser.getId());
            
            return new LoginResponse(token, savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
            
        } catch (Exception e) {
            return new LoginResponse("Registration failed: " + e.getMessage());
        }
    }
}
