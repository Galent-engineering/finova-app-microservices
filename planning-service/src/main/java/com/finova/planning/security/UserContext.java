package com.finova.planning.security;

import com.finova.planning.dto.UserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Optional;

/**
 * Utility class for managing user context in SSO environment
 */
public class UserContext {

    private static final String USER_ID_CLAIM = "user_id";
    private static final String SUB_CLAIM = "sub";
    private static final String EMAIL_CLAIM = "email";
    private static final String USERNAME_CLAIM = "preferred_username";
    private static final String GIVEN_NAME_CLAIM = "given_name";
    private static final String FAMILY_NAME_CLAIM = "family_name";
    private static final String NAME_CLAIM = "name";
    private static final String ROLES_CLAIM = "roles";
    private static final String AUTHORITIES_CLAIM = "authorities";
    private static final String TENANT_ID_CLAIM = "tenant_id";

    /**
     * Get the currently authenticated user
     */
    public static Optional<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
            return Optional.of(extractUserFromJwt(jwtAuth.getToken()));
        }

        return Optional.empty();
    }

    /**
     * Get the current user ID
     */
    public static Optional<Long> getCurrentUserId() {
        return getCurrentUser().map(UserDTO::getUserId);
    }

    /**
     * Get the current username
     */
    public static Optional<String> getCurrentUsername() {
        return getCurrentUser().map(UserDTO::getUsername);
    }

    /**
     * Get the current user's email
     */
    public static Optional<String> getCurrentUserEmail() {
        return getCurrentUser().map(UserDTO::getEmail);
    }

    /**
     * Get the current user's tenant ID
     */
    public static Optional<String> getCurrentTenantId() {
        return getCurrentUser().map(UserDTO::getTenantId);
    }

    /**
     * Check if current user has a specific role
     */
    public static boolean hasRole(String role) {
        return getCurrentUser()
                .map(user -> user.hasRole(role))
                .orElse(false);
    }

    /**
     * Check if current user has a specific authority
     */
    public static boolean hasAuthority(String authority) {
        return getCurrentUser()
                .map(user -> user.hasAuthority(authority))
                .orElse(false);
    }

    /**
     * Check if user is authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * Get the JWT token from current authentication
     */
    public static Optional<Jwt> getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication instanceof JwtAuthenticationToken) {
            return Optional.of(((JwtAuthenticationToken) authentication).getToken());
        }
        
        return Optional.empty();
    }

    /**
     * Extract user information from JWT token
     */
    private static UserDTO extractUserFromJwt(Jwt jwt) {
        UserDTO user = new UserDTO();

        // Extract user ID (try user_id first, then sub)
        Long userId = jwt.getClaimAsString(USER_ID_CLAIM) != null ? 
                      Long.valueOf(jwt.getClaimAsString(USER_ID_CLAIM)) :
                      extractUserIdFromSub(jwt.getClaimAsString(SUB_CLAIM));
        user.setUserId(userId);

        // Extract username
        String username = jwt.getClaimAsString(USERNAME_CLAIM);
        if (username == null) {
            username = jwt.getClaimAsString(EMAIL_CLAIM);
        }
        if (username == null) {
            username = jwt.getClaimAsString(SUB_CLAIM);
        }
        user.setUsername(username);

        // Extract email
        user.setEmail(jwt.getClaimAsString(EMAIL_CLAIM));

        // Extract names
        user.setFirstName(jwt.getClaimAsString(GIVEN_NAME_CLAIM));
        user.setLastName(jwt.getClaimAsString(FAMILY_NAME_CLAIM));
        
        String fullName = jwt.getClaimAsString(NAME_CLAIM);
        if (fullName != null) {
            user.setFullName(fullName);
        }

        // Extract roles
        List<String> roles = jwt.getClaimAsStringList(ROLES_CLAIM);
        user.setRoles(roles);

        // Extract authorities
        List<String> authorities = jwt.getClaimAsStringList(AUTHORITIES_CLAIM);
        user.setAuthorities(authorities);

        // Extract tenant ID
        user.setTenantId(jwt.getClaimAsString(TENANT_ID_CLAIM));

        user.setActive(true);

        return user;
    }

    /**
     * Extract user ID from subject claim if it's numeric
     */
    private static Long extractUserIdFromSub(String sub) {
        if (sub == null) {
            return null;
        }
        
        try {
            return Long.valueOf(sub);
        } catch (NumberFormatException e) {
            // If sub is not numeric, generate a hash-based ID
            return (long) Math.abs(sub.hashCode());
        }
    }

    /**
     * Require authenticated user or throw exception
     */
    public static UserDTO requireCurrentUser() {
        return getCurrentUser()
                .orElseThrow(() -> new SecurityException("User not authenticated"));
    }

    /**
     * Require specific role or throw exception
     */
    public static void requireRole(String role) {
        if (!hasRole(role)) {
            throw new SecurityException("User does not have required role: " + role);
        }
    }

    /**
     * Require specific authority or throw exception
     */
    public static void requireAuthority(String authority) {
        if (!hasAuthority(authority)) {
            throw new SecurityException("User does not have required authority: " + authority);
        }
    }
}
