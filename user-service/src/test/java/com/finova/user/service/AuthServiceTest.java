package com.finova.user.service;

import com.finova.user.dto.LoginRequest;
import com.finova.user.dto.LoginResponse;
import com.finova.user.dto.RegisterRequest;
import com.finova.user.model.User;
import com.finova.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = createTestUser();
        loginRequest = createLoginRequest();
        registerRequest = createRegisterRequest();
    }

    @Test
    void authenticate_WhenValidCredentials_ShouldReturnSuccessResponse() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken("testuser", 1L)).thenReturn("jwt-token");

        // When
        LoginResponse result = authService.authenticate(loginRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("jwt-token", result.getToken());
        assertEquals(1L, result.getUserId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertNull(result.getErrorMessage());
        
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", testUser.getPassword());
        verify(jwtService).generateToken("testuser", 1L);
        verify(userRepository).save(testUser);
    }

    @Test
    void authenticate_WhenUserNotFound_ShouldReturnErrorResponse() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When
        LoginResponse result = authService.authenticate(loginRequest);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Invalid username or password", result.getErrorMessage());
        assertNull(result.getToken());
        
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString(), anyLong());
    }

    @Test
    void authenticate_WhenInvalidPassword_ShouldReturnErrorResponse() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        // When
        LoginResponse result = authService.authenticate(loginRequest);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Invalid username or password", result.getErrorMessage());
        assertNull(result.getToken());
        
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", testUser.getPassword());
        verify(jwtService, never()).generateToken(anyString(), anyLong());
    }

    @Test
    void authenticate_WhenUserDisabled_ShouldReturnErrorResponse() {
        // Given
        testUser.setEnabled(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);

        // When
        LoginResponse result = authService.authenticate(loginRequest);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Account is disabled", result.getErrorMessage());
        assertNull(result.getToken());
        
        verify(userRepository).findByUsername("testuser");
        verify(passwordEncoder).matches("password123", testUser.getPassword());
        verify(jwtService, never()).generateToken(anyString(), anyLong());
    }

    @Test
    void authenticate_WhenExceptionOccurs_ShouldReturnErrorResponse() {
        // Given
        when(userRepository.findByUsername("testuser")).thenThrow(new RuntimeException("Database error"));

        // When
        LoginResponse result = authService.authenticate(loginRequest);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("Authentication failed"));
        assertTrue(result.getErrorMessage().contains("Database error"));
        assertNull(result.getToken());
    }

    @Test
    void register_WhenValidData_ShouldReturnSuccessResponse() {
        // Given
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken("newuser", 1L)).thenReturn("jwt-token");

        // When
        LoginResponse result = authService.register(registerRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("jwt-token", result.getToken());
        assertEquals(1L, result.getUserId());
        assertEquals("newuser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertNull(result.getErrorMessage());
        
        verify(userRepository).findByUsername("newuser");
        verify(userRepository).findByEmail("new@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken("newuser", 1L);
    }

    @Test
    void register_WhenUsernameExists_ShouldReturnErrorResponse() {
        // Given
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(testUser));

        // When
        LoginResponse result = authService.register(registerRequest);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Username already exists", result.getErrorMessage());
        assertNull(result.getToken());
        
        verify(userRepository).findByUsername("newuser");
        verify(userRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_WhenEmailExists_ShouldReturnErrorResponse() {
        // Given
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(testUser));

        // When
        LoginResponse result = authService.register(registerRequest);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Email already exists", result.getErrorMessage());
        assertNull(result.getToken());
        
        verify(userRepository).findByUsername("newuser");
        verify(userRepository).findByEmail("new@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_WhenExceptionOccurs_ShouldReturnErrorResponse() {
        // Given
        when(userRepository.findByUsername("newuser")).thenThrow(new RuntimeException("Database error"));

        // When
        LoginResponse result = authService.register(registerRequest);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getErrorMessage().contains("Registration failed"));
        assertTrue(result.getErrorMessage().contains("Database error"));
        assertNull(result.getToken());
    }

    @Test
    void register_WhenEmailIsNull_ShouldNotCheckEmailExists() {
        // Given
        registerRequest.setEmail(null);
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken("newuser", 1L)).thenReturn("jwt-token");

        // When
        LoginResponse result = authService.register(registerRequest);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        
        verify(userRepository).findByUsername("newuser");
        verify(userRepository, never()).findByEmail(anyString());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken("newuser", 1L);
    }

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("test@example.com");
        user.setEnabled(true);
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    private LoginRequest createLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        return request;
    }

    private RegisterRequest createRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("new@example.com");
        return request;
    }
}
