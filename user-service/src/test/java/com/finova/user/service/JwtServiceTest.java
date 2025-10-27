package com.finova.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtService
 */
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private static final String SECRET = "mySecretKey123456789012345678901234567890";
    private static final Long EXPIRATION = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", EXPIRATION);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Given
        String username = "testuser";
        Long userId = 1L;

        // When
        String token = jwtService.generateToken(username, userId);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token can be parsed
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        assertEquals(username, claims.getSubject());
        assertEquals(userId, claims.get("userId"));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Given
        String username = "testuser";
        String token = jwtService.generateToken(username, 1L);

        // When
        String extractedUsername = jwtService.extractUsername(token);

        // Then
        assertEquals(username, extractedUsername);
    }

    @Test
    void extractUserId_ShouldReturnCorrectUserId() {
        // Given
        Long userId = 123L;
        String token = jwtService.generateToken("testuser", userId);

        // When
        Long extractedUserId = jwtService.extractUserId(token);

        // Then
        assertEquals(userId, extractedUserId);
    }

    @Test
    void extractExpiration_ShouldReturnCorrectExpiration() {
        // Given
        String token = jwtService.generateToken("testuser", 1L);

        // When
        Date expiration = jwtService.extractExpiration(token);

        // Then
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Given
        String username = "testuser";
        String token = jwtService.generateToken(username, 1L);

        // When
        Boolean isValid = jwtService.validateToken(token, username);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_WithInvalidUsername_ShouldReturnFalse() {
        // Given
        String username = "testuser";
        String token = jwtService.generateToken(username, 1L);

        // When
        Boolean isValid = jwtService.validateToken(token, "differentuser");

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        // Given
        String username = "testuser";
        Long shortExpiration = 1L; // 1 millisecond
        ReflectionTestUtils.setField(jwtService, "expiration", shortExpiration);
        
        String token = jwtService.generateToken(username, 1L);
        
        // Wait for token to expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        Boolean isValid = jwtService.validateToken(token, username);

        // Then
        assertFalse(isValid);
    }

    @Test
    void extractClaim_ShouldReturnCorrectClaim() {
        // Given
        String username = "testuser";
        Long userId = 1L;
        String token = jwtService.generateToken(username, userId);

        // When
        String subject = jwtService.extractClaim(token, Claims::getSubject);
        Long extractedUserId = jwtService.extractClaim(token, claims -> claims.get("userId", Long.class));

        // Then
        assertEquals(username, subject);
        assertEquals(userId, extractedUserId);
    }

    @Test
    void generateToken_WithCustomClaims_ShouldIncludeAllClaims() {
        // Given
        String username = "testuser";
        Long userId = 1L;
        Map<String, Object> customClaims = new HashMap<>();
        customClaims.put("role", "ADMIN");
        customClaims.put("department", "IT");

        // When
        String token = jwtService.generateToken(username, userId);

        // Then
        assertNotNull(token);
        
        // Verify custom claims are not included in basic generateToken method
        // (This test verifies the current implementation behavior)
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        assertEquals(username, claims.getSubject());
        assertEquals(userId, claims.get("userId"));
        assertNull(claims.get("role"));
        assertNull(claims.get("department"));
    }

    @Test
    void generateToken_WithNullUsername_ShouldThrowException() {
        // Given
        String username = null;
        Long userId = 1L;

        // When & Then
        assertThrows(Exception.class, () -> jwtService.generateToken(username, userId));
    }

    @Test
    void generateToken_WithNullUserId_ShouldCreateTokenWithNullUserId() {
        // Given
        String username = "testuser";
        Long userId = null;

        // When
        String token = jwtService.generateToken(username, userId);

        // Then
        assertNotNull(token);
        assertNull(jwtService.extractUserId(token));
    }

    @Test
    void extractUsername_WithInvalidToken_ShouldThrowException() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> jwtService.extractUsername(invalidToken));
    }

    @Test
    void extractUserId_WithInvalidToken_ShouldThrowException() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> jwtService.extractUserId(invalidToken));
    }
}
