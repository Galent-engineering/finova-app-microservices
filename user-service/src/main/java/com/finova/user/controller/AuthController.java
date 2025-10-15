package com.finova.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for Authentication and SSO operations for User Service
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /**
     * Health check endpoint for auth service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "User Service - Authentication");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }

    /**
     * Get current authenticated user information
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        logger.debug("Getting current user information");
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();
        
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            
            // Extract user information from JWT
            Map<String, Object> user = new HashMap<>();
            user.put("username", jwt.getClaimAsString("preferred_username"));
            user.put("email", jwt.getClaimAsString("email"));
            user.put("fullName", jwt.getClaimAsString("name"));
            user.put("firstName", jwt.getClaimAsString("given_name"));
            user.put("lastName", jwt.getClaimAsString("family_name"));
            user.put("userId", jwt.getClaimAsString("user_id"));
            user.put("tenantId", jwt.getClaimAsString("tenant_id"));
            user.put("roles", jwt.getClaimAsStringList("roles"));
            
            response.put("authenticated", true);
            response.put("user", user);
            response.put("message", "User information retrieved successfully");
            
            return ResponseEntity.ok(response);
        } else {
            response.put("authenticated", false);
            response.put("message", "User not authenticated");
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * Validate token
     */
    @GetMapping("/validate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> validateToken() {
        logger.debug("Validating JWT token");
        
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            
            response.put("valid", true);
            response.put("authenticated", true);
            response.put("expiresAt", jwt.getExpiresAt().getEpochSecond());
            
            return ResponseEntity.ok(response);
        } else {
            response.put("valid", false);
            response.put("authenticated", false);
            response.put("message", "Token is invalid or expired");
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * Get user roles and authorities
     */
    @GetMapping("/roles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getUserRoles() {
        logger.debug("Getting user roles and authorities");
        
        Map<String, Object> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            
            response.put("roles", jwt.getClaimAsStringList("roles"));
            response.put("authorities", jwt.getClaimAsStringList("scope"));
            
            return ResponseEntity.ok(response);
        } else {
            response.put("roles", java.util.Collections.emptyList());
            response.put("authorities", java.util.Collections.emptyList());
            return ResponseEntity.ok(response);
        }
    }
}
