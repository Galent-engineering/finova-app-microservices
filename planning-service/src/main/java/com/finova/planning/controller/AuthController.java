package com.finova.planning.controller;

import com.finova.planning.dto.AuthenticationResponse;
import com.finova.planning.dto.UserDTO;
import com.finova.planning.security.UserContext;
import com.finova.planning.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Authentication and SSO operations
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Health check endpoint for auth service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Planning Service - Authentication");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }

    /**
     * Get current authenticated user information
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthenticationResponse> getCurrentUser() {
        logger.debug("Getting current user information");
        AuthenticationResponse response = authenticationService.getCurrentUser();
        
        if (response.isAuthenticated()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * Get authentication status and summary
     */
    @GetMapping("/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthenticationResponse> getAuthenticationStatus() {
        logger.debug("Getting authentication status");
        AuthenticationResponse response = authenticationService.getAuthenticationSummary();
        
        if (response.isAuthenticated()) {
            return ResponseEntity.ok(response);
        } else {
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
        boolean isValid = authenticationService.isTokenValid();
        Optional<Long> expiresIn = authenticationService.getTokenExpirationTime();
        
        response.put("valid", isValid);
        response.put("authenticated", authenticationService.isAuthenticated());
        expiresIn.ifPresent(exp -> response.put("expiresAt", exp));
        
        if (isValid) {
            return ResponseEntity.ok(response);
        } else {
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
        Optional<List<String>> roles = authenticationService.getCurrentUserRoles();
        Optional<List<String>> authorities = authenticationService.getCurrentUserAuthorities();
        
        response.put("roles", roles.orElse(java.util.Collections.emptyList()));
        response.put("authorities", authorities.orElse(java.util.Collections.emptyList()));
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if user has specific role
     */
    @GetMapping("/has-role/{role}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> hasRole(@PathVariable String role) {
        logger.debug("Checking if user has role: {}", role);
        
        Map<String, Object> response = new HashMap<>();
        boolean hasRole = authenticationService.hasRole(role);
        
        response.put("role", role);
        response.put("hasRole", hasRole);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Check if user has specific authority
     */
    @GetMapping("/has-authority/{authority}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> hasAuthority(@PathVariable String authority) {
        logger.debug("Checking if user has authority: {}", authority);
        
        Map<String, Object> response = new HashMap<>();
        boolean hasAuthority = authenticationService.hasAuthority(authority);
        
        response.put("authority", authority);
        response.put("hasAuthority", hasAuthority);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Validate user access to specific user resource
     */
    @GetMapping("/validate-access/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> validateUserAccess(@PathVariable Long userId) {
        logger.debug("Validating user access for userId: {}", userId);
        
        Map<String, Object> response = new HashMap<>();
        boolean hasAccess = authenticationService.validateUserAccess(userId);
        
        response.put("userId", userId);
        response.put("hasAccess", hasAccess);
        
        if (!hasAccess) {
            response.put("message", "Access denied to resource");
            return ResponseEntity.status(403).body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get current tenant information
     */
    @GetMapping("/tenant")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getTenantInfo() {
        logger.debug("Getting current tenant information");
        
        Map<String, Object> response = new HashMap<>();
        Optional<String> tenantId = authenticationService.getCurrentTenantId();
        
        response.put("tenantId", tenantId.orElse(null));
        response.put("hasTenant", tenantId.isPresent());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Logout current user
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthenticationResponse> logout() {
        logger.info("User logout requested");
        
        AuthenticationResponse response = authenticationService.logout();
        return ResponseEntity.ok(response);
    }

    /**
     * Get JWT claims
     */
    @GetMapping("/claims")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Map<String, Object>> getJwtClaims() {
        logger.debug("Getting JWT claims (admin only)");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<UserDTO> user = UserContext.getCurrentUser();
            if (user.isPresent()) {
                response.put("user", user.get());
            }
            
            // Get specific claims
            response.put("sub", authenticationService.getJwtClaimAsString("sub").orElse(null));
            response.put("iss", authenticationService.getJwtClaimAsString("iss").orElse(null));
            response.put("aud", authenticationService.getJwtClaimAsString("aud").orElse(null));
            response.put("exp", authenticationService.getTokenExpirationTime().orElse(null));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting JWT claims", e);
            response.put("error", "Error retrieving JWT claims: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Refresh token endpoint (placeholder for future implementation)
     */
    @PostMapping("/refresh")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AuthenticationResponse> refreshToken() {
        logger.debug("Token refresh requested");
        
        // This would typically involve calling an external OAuth provider
        // For now, return current authentication status
        AuthenticationResponse response = authenticationService.getAuthenticationSummary();
        response.setMessage("Token refresh not implemented - using current token");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get authentication configuration info (for debugging)
     */
    @GetMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAuthConfig() {
        logger.debug("Getting authentication configuration (admin only)");
        
        Map<String, Object> response = new HashMap<>();
        response.put("authEnabled", true);
        response.put("authType", "OAuth 2.0 / JWT");
        response.put("roles", "Extracted from JWT 'roles' claim");
        response.put("authorities", "Extracted from JWT 'authorities' and 'scope' claims");
        response.put("principalClaim", "preferred_username");
        
        return ResponseEntity.ok(response);
    }
}
