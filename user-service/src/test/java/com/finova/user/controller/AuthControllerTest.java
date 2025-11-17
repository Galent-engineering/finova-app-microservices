package com.finova.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthController
 * Tests OAuth2/JWT authentication endpoints
 */
@WebMvcTest(AuthController.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityContext securityContext;

    @MockBean
    private Authentication authentication;

    private Jwt mockJwt;

    @BeforeEach
    void setUp() {
        // Create a mock JWT with claims
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "RS256");
        headers.put("typ", "JWT");

        Map<String, Object> claims = new HashMap<>();
        claims.put("preferred_username", "testuser");
        claims.put("email", "test@example.com");
        claims.put("name", "Test User");
        claims.put("given_name", "Test");
        claims.put("family_name", "User");
        claims.put("user_id", "123");
        claims.put("tenant_id", "tenant-1");
        claims.put("roles", Arrays.asList("USER", "ADMIN"));
        claims.put("scope", Arrays.asList("read", "write"));

        mockJwt = new Jwt(
            "mock-token-value",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            headers,
            claims
        );
    }

    @Test
    @DisplayName("GET /api/auth/health - Should return health status")
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/auth/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("User Service - Authentication"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("GET /api/auth/me - Should return current user info when authenticated")
    void testGetCurrentUserAuthenticated() throws Exception {
        // Mock the security context
        when(authentication.getPrincipal()).thenReturn(mockJwt);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.fullName").value("Test User"))
                .andExpect(jsonPath("$.user.firstName").value("Test"))
                .andExpect(jsonPath("$.user.lastName").value("User"))
                .andExpect(jsonPath("$.user.userId").value("123"))
                .andExpect(jsonPath("$.user.tenantId").value("tenant-1"))
                .andExpect(jsonPath("$.user.roles[0]").value("USER"))
                .andExpect(jsonPath("$.user.roles[1]").value("ADMIN"))
                .andExpect(jsonPath("$.message").value("User information retrieved successfully"));

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /api/auth/me - Should return 401 when not authenticated")
    void testGetCurrentUserNotAuthenticated() throws Exception {
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.message").value("User not authenticated"));

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /api/auth/me - Should handle null authentication")
    void testGetCurrentUserNullAuthentication() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.message").value("User not authenticated"));

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /api/auth/validate - Should validate token successfully")
    void testValidateTokenSuccess() throws Exception {
        when(authentication.getPrincipal()).thenReturn(mockJwt);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/api/auth/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.expiresAt").exists());

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /api/auth/validate - Should return invalid for unauthenticated request")
    void testValidateTokenInvalid() throws Exception {
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        when(authentication.isAuthenticated()).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/api/auth/validate"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.message").value("Token is invalid or expired"));

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /api/auth/validate - Should handle null authentication")
    void testValidateTokenNullAuthentication() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/api/auth/validate"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.authenticated").value(false));

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /api/auth/roles - Should return user roles and authorities")
    void testGetUserRoles() throws Exception {
        when(authentication.getPrincipal()).thenReturn(mockJwt);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/api/auth/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[0]").value("USER"))
                .andExpect(jsonPath("$.roles[1]").value("ADMIN"))
                .andExpect(jsonPath("$.authorities[0]").value("read"))
                .andExpect(jsonPath("$.authorities[1]").value("write"));

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /api/auth/roles - Should return empty lists when not authenticated")
    void testGetUserRolesNotAuthenticated() throws Exception {
        when(authentication.getPrincipal()).thenReturn("anonymousUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/api/auth/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles").isEmpty())
                .andExpect(jsonPath("$.authorities").isEmpty());

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /api/auth/roles - Should handle null authentication")
    void testGetUserRolesNullAuthentication() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/api/auth/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles").isEmpty())
                .andExpect(jsonPath("$.authorities").isEmpty());

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /api/auth/me - Should handle JWT with missing claims")
    void testGetCurrentUserWithMissingClaims() throws Exception {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "RS256");

        Map<String, Object> minimalClaims = new HashMap<>();
        minimalClaims.put("preferred_username", "testuser");

        Jwt minimalJwt = new Jwt(
            "mock-token-value",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            headers,
            minimalClaims
        );

        when(authentication.getPrincipal()).thenReturn(minimalJwt);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.user.email").doesNotExist())
                .andExpect(jsonPath("$.user.fullName").doesNotExist());

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("GET /api/auth/roles - Should handle JWT with null roles")
    void testGetUserRolesWithNullRoles() throws Exception {
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "RS256");

        Map<String, Object> claimsWithoutRoles = new HashMap<>();
        claimsWithoutRoles.put("preferred_username", "testuser");

        Jwt jwtWithoutRoles = new Jwt(
            "mock-token-value",
            Instant.now(),
            Instant.now().plusSeconds(3600),
            headers,
            claimsWithoutRoles
        );

        when(authentication.getPrincipal()).thenReturn(jwtWithoutRoles);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/api/auth/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles").doesNotExist())
                .andExpect(jsonPath("$.authorities").doesNotExist());

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should handle CORS preflight requests")
    void testCorsPreflightRequest() throws Exception {
        mockMvc.perform(options("/api/auth/health")
                .header("Access-Control-Request-Method", "GET")
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/auth/validate - Should extract expiration time correctly")
    void testValidateTokenExpirationTime() throws Exception {
        Instant expiresAt = Instant.now().plusSeconds(3600);
        
        Map<String, Object> headers = new HashMap<>();
        headers.put("alg", "RS256");

        Map<String, Object> claims = new HashMap<>();
        claims.put("preferred_username", "testuser");

        Jwt jwtWithExpiration = new Jwt(
            "mock-token-value",
            Instant.now(),
            expiresAt,
            headers,
            claims
        );

        when(authentication.getPrincipal()).thenReturn(jwtWithExpiration);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        mockMvc.perform(get("/api/auth/validate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.expiresAt").value(expiresAt.getEpochSecond()));

        SecurityContextHolder.clearContext();
    }
}
