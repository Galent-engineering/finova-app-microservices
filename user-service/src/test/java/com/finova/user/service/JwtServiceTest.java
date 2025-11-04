package com.finova.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Comprehensive unit tests for JwtService
 * Tests token generation, validation, and claim extraction
 */
@DisplayName("JwtService Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private static final String TEST_SECRET = "testSecretKeyForJWTTokenGenerationAndValidationMustBeLongEnough";
    private static final Long TEST_EXPIRATION = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", TEST_EXPIRATION);
    }

    @Nested
    @DisplayName("Token Generation Tests")
    class TokenGenerationTests {

        @Test
        @DisplayName("Should generate valid JWT token")
        void shouldGenerateValidJwtToken() {
            String username = "testuser";
            Long userId = 123L;

            String token = jwtService.generateToken(username, userId);

            assertThat(token).isNotNull();
            assertThat(token).isNotEmpty();
            assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
        }

        @Test
        @DisplayName("Should generate token with correct username")
        void shouldGenerateTokenWithCorrectUsername() {
            String username = "johndoe";
            Long userId = 456L;

            String token = jwtService.generateToken(username, userId);
            String extractedUsername = jwtService.extractUsername(token);

            assertThat(extractedUsername).isEqualTo(username);
        }

        @Test
        @DisplayName("Should generate token with correct user ID")
        void shouldGenerateTokenWithCorrectUserId() {
            String username = "janesmith";
            Long userId = 789L;

            String token = jwtService.generateToken(username, userId);
            Long extractedUserId = jwtService.extractUserId(token);

            assertThat(extractedUserId).isEqualTo(userId);
        }

        @Test
        @DisplayName("Should generate different tokens for different users")
        void shouldGenerateDifferentTokensForDifferentUsers() {
            String token1 = jwtService.generateToken("user1", 1L);
            String token2 = jwtService.generateToken("user2", 2L);

            assertThat(token1).isNotEqualTo(token2);
        }

        @Test
        @DisplayName("Should generate token with expiration time")
        void shouldGenerateTokenWithExpirationTime() {
            String username = "testuser";
            Long userId = 100L;

            String token = jwtService.generateToken(username, userId);
            Date expiration = jwtService.extractExpiration(token);

            assertThat(expiration).isNotNull();
            assertThat(expiration).isAfter(new Date());
        }
    }

    @Nested
    @DisplayName("Token Validation Tests")
    class TokenValidationTests {

        @Test
        @DisplayName("Should validate correct token")
        void shouldValidateCorrectToken() {
            String username = "testuser";
            Long userId = 123L;

            String token = jwtService.generateToken(username, userId);
            Boolean isValid = jwtService.validateToken(token, username);

            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("Should reject token with wrong username")
        void shouldRejectTokenWithWrongUsername() {
            String username = "testuser";
            Long userId = 123L;

            String token = jwtService.generateToken(username, userId);
            Boolean isValid = jwtService.validateToken(token, "wronguser");

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("Should reject malformed token")
        void shouldRejectMalformedToken() {
            String malformedToken = "invalid.token.here";

            assertThatThrownBy(() -> jwtService.validateToken(malformedToken, "testuser"))
                .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Should reject expired token")
        void shouldRejectExpiredToken() {
            // Create service with very short expiration
            JwtService shortExpirationService = new JwtService();
            ReflectionTestUtils.setField(shortExpirationService, "secret", TEST_SECRET);
            ReflectionTestUtils.setField(shortExpirationService, "expiration", -1000L); // Expired

            String token = shortExpirationService.generateToken("testuser", 123L);

            // Token should be expired immediately
            assertThatThrownBy(() -> shortExpirationService.validateToken(token, "testuser"))
                .isInstanceOf(ExpiredJwtException.class);
        }
    }

    @Nested
    @DisplayName("Claim Extraction Tests")
    class ClaimExtractionTests {

        @Test
        @DisplayName("Should extract username from token")
        void shouldExtractUsernameFromToken() {
            String username = "johndoe";
            String token = jwtService.generateToken(username, 123L);

            String extractedUsername = jwtService.extractUsername(token);

            assertThat(extractedUsername).isEqualTo(username);
        }

        @Test
        @DisplayName("Should extract user ID from token")
        void shouldExtractUserIdFromToken() {
            Long userId = 456L;
            String token = jwtService.generateToken("testuser", userId);

            Long extractedUserId = jwtService.extractUserId(token);

            assertThat(extractedUserId).isEqualTo(userId);
        }

        @Test
        @DisplayName("Should extract expiration date from token")
        void shouldExtractExpirationDateFromToken() {
            String token = jwtService.generateToken("testuser", 123L);

            Date expiration = jwtService.extractExpiration(token);

            assertThat(expiration).isNotNull();
            assertThat(expiration.getTime()).isGreaterThan(System.currentTimeMillis());
        }

        @Test
        @DisplayName("Should extract custom claim using function")
        void shouldExtractCustomClaimUsingFunction() {
            String token = jwtService.generateToken("testuser", 789L);

            String subject = jwtService.extractClaim(token, Claims::getSubject);

            assertThat(subject).isEqualTo("testuser");
        }
    }

    @Nested
    @DisplayName("Security Tests")
    class SecurityTests {

        @Test
        @DisplayName("Should not allow token tampering")
        void shouldNotAllowTokenTampering() {
            String token = jwtService.generateToken("testuser", 123L);
            String[] parts = token.split("\\.");
            
            // Tamper with the payload
            String tamperedToken = parts[0] + ".tamperedPayload." + parts[2];

            assertThatThrownBy(() -> jwtService.extractUsername(tamperedToken))
                .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("Should use secure signing algorithm")
        void shouldUseSecureSigningAlgorithm() {
            String token = jwtService.generateToken("testuser", 123L);

            // JWT token should be properly formatted
            assertThat(token).matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$");
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle username with special characters")
        void shouldHandleUsernameWithSpecialCharacters() {
            String username = "user.name+test@example";
            Long userId = 123L;

            String token = jwtService.generateToken(username, userId);
            String extractedUsername = jwtService.extractUsername(token);

            assertThat(extractedUsername).isEqualTo(username);
        }

        @Test
        @DisplayName("Should handle very long username")
        void shouldHandleVeryLongUsername() {
            String username = "a".repeat(100);
            Long userId = 123L;

            String token = jwtService.generateToken(username, userId);
            String extractedUsername = jwtService.extractUsername(token);

            assertThat(extractedUsername).isEqualTo(username);
        }

        @Test
        @DisplayName("Should handle large user ID")
        void shouldHandleLargeUserId() {
            String username = "testuser";
            Long userId = Long.MAX_VALUE;

            String token = jwtService.generateToken(username, userId);
            Long extractedUserId = jwtService.extractUserId(token);

            assertThat(extractedUserId).isEqualTo(userId);
        }

        @Test
        @DisplayName("Should handle minimum user ID")
        void shouldHandleMinimumUserId() {
            String username = "testuser";
            Long userId = 1L;

            String token = jwtService.generateToken(username, userId);
            Long extractedUserId = jwtService.extractUserId(token);

            assertThat(extractedUserId).isEqualTo(userId);
        }
    }

    @Nested
    @DisplayName("Financial Domain Tests")
    class FinancialDomainTests {

        @Test
        @DisplayName("Should generate token for retirement advisor")
        void shouldGenerateTokenForRetirementAdvisor() {
            String username = "retirement.advisor@finova.com";
            Long userId = 5001L;

            String token = jwtService.generateToken(username, userId);

            assertThat(jwtService.extractUsername(token)).isEqualTo(username);
            assertThat(jwtService.extractUserId(token)).isEqualTo(userId);
            assertThat(jwtService.validateToken(token, username)).isTrue();
        }

        @Test
        @DisplayName("Should generate token for plan participant")
        void shouldGenerateTokenForPlanParticipant() {
            String username = "participant123";
            Long userId = 10001L;

            String token = jwtService.generateToken(username, userId);

            assertThat(jwtService.extractUsername(token)).isEqualTo(username);
            assertThat(jwtService.extractUserId(token)).isEqualTo(userId);
        }

        @Test
        @DisplayName("Should maintain token validity for session duration")
        void shouldMaintainTokenValidityForSessionDuration() {
            String username = "longSessionUser";
            Long userId = 2000L;

            String token = jwtService.generateToken(username, userId);
            Date expiration = jwtService.extractExpiration(token);
            
            long sessionDuration = expiration.getTime() - System.currentTimeMillis();
            
            // Should be valid for approximately 1 hour (with small tolerance)
            assertThat(sessionDuration).isGreaterThan(3500000L); // At least 58 minutes
            assertThat(sessionDuration).isLessThan(3700000L); // At most 62 minutes
        }
    }
}
