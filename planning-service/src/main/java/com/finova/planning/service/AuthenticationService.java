package com.finova.planning.service;

import com.finova.planning.dto.AuthenticationResponse;
import com.finova.planning.dto.UserDTO;
import com.finova.planning.security.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * Service for handling authentication operations in SSO context
 */
@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    private JwtDecoder jwtDecoder;

    /**
     * Get current authenticated user information
     */
    public AuthenticationResponse getCurrentUser() {
        try {
            Optional<UserDTO> currentUser = UserContext.getCurrentUser();
            
            if (currentUser.isPresent()) {
                logger.debug("Retrieved current user: {}", currentUser.get().getUsername());
                return AuthenticationResponse.userInfo(currentUser.get());
            } else {
                logger.warn("No authenticated user found in security context");
                return AuthenticationResponse.failure("No authenticated user found");
            }
        } catch (Exception e) {
            logger.error("Error retrieving current user", e);
            return AuthenticationResponse.failure("Error retrieving user information: " + e.getMessage());
        }
    }

    /**
     * Validate JWT token
     */
    public boolean isTokenValid() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                Jwt jwt = jwtAuth.getToken();
                
                // Check if token is expired
                Instant expiresAt = jwt.getExpiresAt();
                if (expiresAt != null && expiresAt.isBefore(Instant.now())) {
                    logger.warn("JWT token is expired");
                    return false;
                }
                
                // Check if token has required claims
                if (jwt.getClaimAsString("sub") == null) {
                    logger.warn("JWT token missing required 'sub' claim");
                    return false;
                }
                
                logger.debug("JWT token is valid");
                return true;
            }
            
            logger.warn("No JWT authentication found");
            return false;
        } catch (Exception e) {
            logger.error("Error validating JWT token", e);
            return false;
        }
    }

    /**
     * Get token expiration time
     */
    public Optional<Long> getTokenExpirationTime() {
        try {
            Optional<Jwt> jwt = UserContext.getCurrentJwt();
            
            if (jwt.isPresent()) {
                Instant expiresAt = jwt.get().getExpiresAt();
                if (expiresAt != null) {
                    return Optional.of(expiresAt.getEpochSecond());
                }
            }
            
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error getting token expiration time", e);
            return Optional.empty();
        }
    }

    /**
     * Check if current user has specific role
     */
    public boolean hasRole(String role) {
        try {
            boolean hasRole = UserContext.hasRole(role);
            logger.debug("User has role '{}': {}", role, hasRole);
            return hasRole;
        } catch (Exception e) {
            logger.error("Error checking user role: {}", role, e);
            return false;
        }
    }

    /**
     * Check if current user has specific authority
     */
    public boolean hasAuthority(String authority) {
        try {
            boolean hasAuthority = UserContext.hasAuthority(authority);
            logger.debug("User has authority '{}': {}", authority, hasAuthority);
            return hasAuthority;
        } catch (Exception e) {
            logger.error("Error checking user authority: {}", authority, e);
            return false;
        }
    }

    /**
     * Get user roles
     */
    public Optional<java.util.List<String>> getCurrentUserRoles() {
        return UserContext.getCurrentUser().map(UserDTO::getRoles);
    }

    /**
     * Get user authorities
     */
    public Optional<java.util.List<String>> getCurrentUserAuthorities() {
        return UserContext.getCurrentUser().map(UserDTO::getAuthorities);
    }

    /**
     * Logout current user (clear security context)
     */
    public AuthenticationResponse logout() {
        try {
            SecurityContextHolder.clearContext();
            logger.info("User logged out successfully");
            return AuthenticationResponse.success(null, null, null, null);
        } catch (Exception e) {
            logger.error("Error during logout", e);
            return AuthenticationResponse.failure("Error during logout: " + e.getMessage());
        }
    }

    /**
     * Check if user is authenticated
     */
    public boolean isAuthenticated() {
        return UserContext.isAuthenticated();
    }

    /**
     * Get JWT claims as string
     */
    public Optional<String> getJwtClaimAsString(String claimName) {
        try {
            Optional<Jwt> jwt = UserContext.getCurrentJwt();
            if (jwt.isPresent()) {
                return Optional.ofNullable(jwt.get().getClaimAsString(claimName));
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error getting JWT claim: {}", claimName, e);
            return Optional.empty();
        }
    }

    /**
     * Get current tenant ID
     */
    public Optional<String> getCurrentTenantId() {
        return UserContext.getCurrentTenantId();
    }

    /**
     * Validate user access to resource
     */
    public boolean validateUserAccess(Long userId) {
        try {
            Optional<Long> currentUserId = UserContext.getCurrentUserId();
            
            if (currentUserId.isEmpty()) {
                logger.warn("No current user ID found");
                return false;
            }
            
            // User can access their own resources
            if (currentUserId.get().equals(userId)) {
                return true;
            }
            
            // Check if user has admin role
            if (UserContext.hasRole("ADMIN") || UserContext.hasRole("SYSTEM_ADMIN")) {
                return true;
            }
            
            // Check if user has financial advisor role and same tenant
            if (UserContext.hasRole("FINANCIAL_ADVISOR")) {
                // Additional tenant-based authorization logic can be added here
                return true;
            }
            
            logger.warn("User {} does not have access to resource for user {}", 
                       currentUserId.get(), userId);
            return false;
        } catch (Exception e) {
            logger.error("Error validating user access for userId: {}", userId, e);
            return false;
        }
    }

    /**
     * Get authentication summary
     */
    public AuthenticationResponse getAuthenticationSummary() {
        try {
            if (!isAuthenticated()) {
                return AuthenticationResponse.failure("User not authenticated");
            }

            UserDTO user = UserContext.getCurrentUser().orElse(null);
            Long expiresIn = getTokenExpirationTime().orElse(null);
            boolean tokenValid = isTokenValid();

            AuthenticationResponse response = new AuthenticationResponse();
            response.setUser(user);
            response.setExpiresIn(expiresIn);
            response.setAuthenticated(tokenValid);
            response.setMessage(tokenValid ? "Authentication valid" : "Authentication expired or invalid");

            return response;
        } catch (Exception e) {
            logger.error("Error getting authentication summary", e);
            return AuthenticationResponse.failure("Error retrieving authentication summary: " + e.getMessage());
        }
    }
}
