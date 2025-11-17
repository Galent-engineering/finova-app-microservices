package com.finova.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtService
 * Tests JWT token generation, validation, and claims extraction
 */
@DisplayName("JwtService Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private String testSecret;
    private Long testExpiration;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Use a test secret that's at least 256 bits (32 bytes)
        testSecret = "testSecretKeyForJwtTokenGenerationAndValidationThatIsLongEnough123456";
        testExpiration = 3600000L; // 1 hour in milliseconds

        // Use reflection to set private fields
        ReflectionTestUtils.setField(jwtService, "secret", testSecret);
        ReflectionTestUtils.setField(jwtService, "expiration", testExpiration);
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void testGenerateToken() {
        String username = "testuser";
        Long userId = 123L;

        String token = jwtService.generateToken(username, userId);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts separated by dots
    }

    @Test
    @DisplayName("Should extract username from token")
    void testExtractUsername() {
        String username = "testuser";
        Long userId = 123L;

        String token = jwtService.generateToken(username, userId);
        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Should extract user ID from token")
    void testExtractUserId() {
        String username = "testuser";
        Long userId = 123L;

        String token = jwtService.generateToken(username, userId);
        Long extractedUserId = jwtService.extractUserId(token);

        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("Should extract expiration date from token")
    void testExtractExpiration() {
        String username = "testuser";
        Long userId = 123L;

        String token = jwtService.generateToken(username, userId);
        Date expirationDate = jwtService.extractExpiration(token);

        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    @DisplayName("Should validate token with correct username")
    void testValidateTokenSuccess() {
        String username = "testuser";
        Long userId = 123L;

        String token = jwtService.generateToken(username, userId);
        Boolean isValid = jwtService.validateToken(token, username);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should fail validation with incorrect username")
    void testValidateTokenWrongUsername() {
        String username = "testuser";
        Long userId = 123L;

        String token = jwtService.generateToken(username, userId);
        Boolean isValid = jwtService.validateToken(token, "wronguser");

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void testGenerateDifferentTokensForDifferentUsers() {
        String token1 = jwtService.generateToken("user1", 1L);
        String token2 = jwtService.generateToken("user2", 2L);

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Should include userId in token claims")
    void testTokenContainsUserId() {
        String username = "testuser";
        Long userId = 999L;

        String token = jwtService.generateToken(username, userId);
        Long extractedUserId = jwtService.extractUserId(token);

        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("Should set correct expiration time")
    void testTokenExpirationTime() {
        String username = "testuser";
        Long userId = 123L;
        long beforeGeneration = System.currentTimeMillis();

        String token = jwtService.generateToken(username, userId);
        Date expirationDate = jwtService.extractExpiration(token);

        long expectedExpiration = beforeGeneration + testExpiration;
        long actualExpiration = expirationDate.getTime();

        // Allow 1 second tolerance for test execution time
        assertTrue(Math.abs(actualExpiration - expectedExpiration) < 1000);
    }

    @Test
    @DisplayName("Should extract custom claims from token")
    void testExtractCustomClaim() {
        String username = "testuser";
        Long userId = 123L;

        String token = jwtService.generateToken(username, userId);
        
        // Extract the entire claims object
        Claims claims = jwtService.extractClaim(token, claims1 -> claims1);
        
        assertNotNull(claims);
        assertEquals(username, claims.getSubject());
        assertEquals(userId, claims.get("userId", Long.class));
    }

    @Test
    @DisplayName("Should handle null username gracefully")
    void testGenerateTokenWithNullUsername() {
        Long userId = 123L;

        // This should not throw an exception
        assertDoesNotThrow(() -> jwtService.generateToken(null, userId));
    }

    @Test
    @DisplayName("Should handle null userId gracefully")
    void testGenerateTokenWithNullUserId() {
        String username = "testuser";

        // This should not throw an exception
        assertDoesNotThrow(() -> jwtService.generateToken(username, null));
    }

    @Test
    @DisplayName("Should generate token with issued at time")
    void testTokenIssuedAtTime() {
        String username = "testuser";
        Long userId = 123L;
        long beforeGeneration = System.currentTimeMillis();

        String token = jwtService.generateToken(username, userId);
        Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);

        assertNotNull(issuedAt);
        assertTrue(issuedAt.getTime() >= beforeGeneration);
        assertTrue(issuedAt.getTime() <= System.currentTimeMillis());
    }

    @Test
    @DisplayName("Should detect expired token")
    void testExpiredToken() {
        // Create a service with very short expiration
        JwtService shortExpirationService = new JwtService();
        ReflectionTestUtils.setField(shortExpirationService, "secret", testSecret);
        ReflectionTestUtils.setField(shortExpirationService, "expiration", -1000L); // Expired 1 second ago

        String username = "testuser";
        Long userId = 123L;

        String token = shortExpirationService.generateToken(username, userId);

        // Token should be expired
        assertThrows(ExpiredJwtException.class, () -> {
            shortExpirationService.extractUsername(token);
        });
    }

    @Test
    @DisplayName("Should validate token returns false for expired token")
    void testValidateExpiredToken() {
        // Create a service with very short expiration
        JwtService shortExpirationService = new JwtService();
        ReflectionTestUtils.setField(shortExpirationService, "secret", testSecret);
        ReflectionTestUtils.setField(shortExpirationService, "expiration", 1L); // 1 millisecond

        String username = "testuser";
        Long userId = 123L;

        String token = shortExpirationService.generateToken(username, userId);

        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Validation should fail due to expiration
        assertThrows(ExpiredJwtException.class, () -> {
            shortExpirationService.validateToken(token, username);
        });
    }

    @Test
    @DisplayName("Should generate consistent tokens for same input at same time")
    void testTokenConsistency() {
        String username = "testuser";
        Long userId = 123L;

        // Generate two tokens immediately one after another
        String token1 = jwtService.generateToken(username, userId);
        String token2 = jwtService.generateToken(username, userId);

        // Tokens should be different due to different issued-at times
        // but should contain the same username and userId
        assertEquals(jwtService.extractUsername(token1), jwtService.extractUsername(token2));
        assertEquals(jwtService.extractUserId(token1), jwtService.extractUserId(token2));
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void testSpecialCharactersInUsername() {
        String username = "test.user+123@example.com";
        Long userId = 123L;

        String token = jwtService.generateToken(username, userId);
        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Should handle large user IDs")
    void testLargeUserId() {
        String username = "testuser";
        Long userId = Long.MAX_VALUE;

        String token = jwtService.generateToken(username, userId);
        Long extractedUserId = jwtService.extractUserId(token);

        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("Should handle zero user ID")
    void testZeroUserId() {
        String username = "testuser";
        Long userId = 0L;

        String token = jwtService.generateToken(username, userId);
        Long extractedUserId = jwtService.extractUserId(token);

        assertEquals(userId, extractedUserId);
    }

    @Test
    @DisplayName("Should extract all claims from token")
    void testExtractAllClaims() {
        String username = "testuser";
        Long userId = 123L;

        String token = jwtService.generateToken(username, userId);
        Claims claims = jwtService.extractClaim(token, claims1 -> claims1);

        assertNotNull(claims);
        assertNotNull(claims.getSubject());
        assertNotNull(claims.get("userId"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }
}
