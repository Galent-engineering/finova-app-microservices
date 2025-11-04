package com.finova.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtService
 */
@DisplayName("JwtService Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private String testSecret;
    private Long testExpiration;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Use a secret key with at least 256 bits (32 bytes) for HS256
        testSecret = "mySecretKeyForJWTTokenGenerationAndValidationThatIsLongEnough12345";
        testExpiration = 3600000L; // 1 hour in milliseconds

        ReflectionTestUtils.setField(jwtService, "secret", testSecret);
        ReflectionTestUtils.setField(jwtService, "expiration", testExpiration);
    }

    @Test
    @DisplayName("Should generate JWT token successfully")
    void testGenerateToken() {
        String username = "testuser";
        Long userId = 1L;

        String token = jwtService.generateToken(username, userId);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts separated by dots
    }

    @Test
    @DisplayName("Should extract username from token")
    void testExtractUsername() {
        String username = "testuser";
        Long userId = 1L;

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
        Long userId = 1L;

        String token = jwtService.generateToken(username, userId);
        Date expiration = jwtService.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    @DisplayName("Should validate token successfully")
    void testValidateToken_Success() {
        String username = "testuser";
        Long userId = 1L;

        String token = jwtService.generateToken(username, userId);
        Boolean isValid = jwtService.validateToken(token, username);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should fail validation with wrong username")
    void testValidateToken_WrongUsername() {
        String username = "testuser";
        Long userId = 1L;

        String token = jwtService.generateToken(username, userId);
        Boolean isValid = jwtService.validateToken(token, "wronguser");

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should fail validation with expired token")
    void testValidateToken_ExpiredToken() {
        // Set expiration to negative value to create expired token
        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);

        String username = "testuser";
        Long userId = 1L;

        String token = jwtService.generateToken(username, userId);

        // Reset expiration to normal value for validation
        ReflectionTestUtils.setField(jwtService, "expiration", testExpiration);

        Boolean isValid = jwtService.validateToken(token, username);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void testGenerateDifferentTokens() {
        String token1 = jwtService.generateToken("user1", 1L);
        String token2 = jwtService.generateToken("user2", 2L);

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Should extract all claims from token")
    void testExtractClaim() {
        String username = "testuser";
        Long userId = 1L;

        String token = jwtService.generateToken(username, userId);

        String extractedSubject = jwtService.extractClaim(token, Claims::getSubject);
        assertEquals(username, extractedSubject);

        Date extractedExpiration = jwtService.extractClaim(token, Claims::getExpiration);
        assertNotNull(extractedExpiration);
        assertTrue(extractedExpiration.after(new Date()));
    }

    @Test
    @DisplayName("Should handle token with custom claims")
    void testTokenWithCustomClaims() {
        String username = "testuser";
        Long userId = 42L;

        String token = jwtService.generateToken(username, userId);

        Long extractedUserId = jwtService.extractUserId(token);
        assertEquals(42L, extractedUserId);
    }

    @Test
    @DisplayName("Should throw exception for invalid token signature")
    void testInvalidTokenSignature() {
        String username = "testuser";
        Long userId = 1L;

        String token = jwtService.generateToken(username, userId);

        // Change the secret to invalidate the signature
        ReflectionTestUtils.setField(jwtService, "secret", "differentSecretKeyThatIsAlsoLongEnoughForHS256Algorithm");

        assertThrows(SignatureException.class, () -> {
            jwtService.extractUsername(token);
        });
    }

    @Test
    @DisplayName("Should throw exception for malformed token")
    void testMalformedToken() {
        String malformedToken = "this.is.not.a.valid.jwt.token";

        assertThrows(Exception.class, () -> {
            jwtService.extractUsername(malformedToken);
        });
    }

    @Test
    @DisplayName("Should generate token with correct expiration time")
    void testTokenExpirationTime() {
        String username = "testuser";
        Long userId = 1L;

        long beforeGeneration = System.currentTimeMillis();
        String token = jwtService.generateToken(username, userId);
        long afterGeneration = System.currentTimeMillis();

        Date expiration = jwtService.extractExpiration(token);
        long expirationTime = expiration.getTime();

        // Token should expire approximately 1 hour from now
        assertTrue(expirationTime >= beforeGeneration + testExpiration);
        assertTrue(expirationTime <= afterGeneration + testExpiration + 1000); // Allow 1 second tolerance
    }

    @Test
    @DisplayName("Should handle token for user with special characters in username")
    void testTokenWithSpecialCharacters() {
        String username = "user@example.com";
        Long userId = 1L;

        String token = jwtService.generateToken(username, userId);
        String extractedUsername = jwtService.extractUsername(token);

        assertEquals(username, extractedUsername);
    }

    @Test
    @DisplayName("Should validate token is not expired immediately after creation")
    void testTokenNotExpiredAfterCreation() {
        String username = "testuser";
        Long userId = 1L;

        String token = jwtService.generateToken(username, userId);
        Date expiration = jwtService.extractExpiration(token);

        assertTrue(expiration.after(new Date()));
    }

    @Test
    @DisplayName("Should generate consistent token structure")
    void testTokenStructure() {
        String username = "testuser";
        Long userId = 1L;

        String token = jwtService.generateToken(username, userId);

        // JWT should have 3 parts: header.payload.signature
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);

        // Each part should be non-empty
        for (String part : parts) {
            assertFalse(part.isEmpty());
        }
    }

    @Test
    @DisplayName("Should handle zero user ID")
    void testTokenWithZeroUserId() {
        String username = "testuser";
        Long userId = 0L;

        String token = jwtService.generateToken(username, userId);
        Long extractedUserId = jwtService.extractUserId(token);

        assertEquals(0L, extractedUserId);
    }

    @Test
    @DisplayName("Should handle large user ID")
    void testTokenWithLargeUserId() {
        String username = "testuser";
        Long userId = Long.MAX_VALUE;

        String token = jwtService.generateToken(username, userId);
        Long extractedUserId = jwtService.extractUserId(token);

        assertEquals(Long.MAX_VALUE, extractedUserId);
    }
}
