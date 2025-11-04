package com.finova.user.service;

import com.finova.user.dto.LoginRequest;
import com.finova.user.dto.LoginResponse;
import com.finova.user.dto.RegisterRequest;
import com.finova.user.model.User;
import com.finova.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
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
        testUser = new User("testuser", "encodedPassword123", "John", "Doe", "john.doe@example.com");
        testUser.setId(1L);
        testUser.setEnabled(true);

        loginRequest = new LoginRequest("testuser", "password123");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("newpassword123");
        registerRequest.setFirstName("Jane");
        registerRequest.setLastName("Smith");
        registerRequest.setEmail("jane.smith@example.com");
    }

    @Test
    @DisplayName("Should authenticate user successfully")
    void testAuthenticate_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(jwtService.generateToken("testuser", 1L)).thenReturn("jwt-token-123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        LoginResponse response = authService.authenticate(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token-123", response.getToken());
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals("Login successful", response.getMessage());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword123");
        verify(jwtService, times(1)).generateToken("testuser", 1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail authentication with invalid username")
    void testAuthenticate_InvalidUsername() {
        when(userRepository.findByUsername("invaliduser")).thenReturn(Optional.empty());

        LoginRequest invalidRequest = new LoginRequest("invaliduser", "password123");
        LoginResponse response = authService.authenticate(invalidRequest);

        assertNotNull(response);
        assertNull(response.getToken());
        assertEquals("Invalid username or password", response.getMessage());

        verify(userRepository, times(1)).findByUsername("invaliduser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("Should fail authentication with invalid password")
    void testAuthenticate_InvalidPassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "encodedPassword123")).thenReturn(false);

        LoginRequest invalidRequest = new LoginRequest("testuser", "wrongpassword");
        LoginResponse response = authService.authenticate(invalidRequest);

        assertNotNull(response);
        assertNull(response.getToken());
        assertEquals("Invalid username or password", response.getMessage());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("wrongpassword", "encodedPassword123");
        verify(jwtService, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("Should fail authentication when account is disabled")
    void testAuthenticate_AccountDisabled() {
        testUser.setEnabled(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);

        LoginResponse response = authService.authenticate(loginRequest);

        assertNotNull(response);
        assertNull(response.getToken());
        assertEquals("Account is disabled", response.getMessage());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword123");
        verify(jwtService, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("Should register new user successfully")
    void testRegister_Success() {
        User newUser = new User("newuser", "encodedNewPassword123", "Jane", "Smith", "jane.smith@example.com");
        newUser.setId(2L);

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("jane.smith@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpassword123")).thenReturn("encodedNewPassword123");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(jwtService.generateToken("newuser", 2L)).thenReturn("jwt-token-456");

        LoginResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token-456", response.getToken());
        assertEquals(2L, response.getId());
        assertEquals("newuser", response.getUsername());
        assertEquals("jane.smith@example.com", response.getEmail());
        assertEquals("Login successful", response.getMessage());

        verify(userRepository, times(1)).findByUsername("newuser");
        verify(userRepository, times(1)).findByEmail("jane.smith@example.com");
        verify(passwordEncoder, times(1)).encode("newpassword123");
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken("newuser", 2L);
    }

    @Test
    @DisplayName("Should fail registration when username already exists")
    void testRegister_UsernameExists() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(testUser));

        LoginResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertNull(response.getToken());
        assertEquals("Username already exists", response.getMessage());

        verify(userRepository, times(1)).findByUsername("newuser");
        verify(userRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail registration when email already exists")
    void testRegister_EmailExists() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("jane.smith@example.com")).thenReturn(Optional.of(testUser));

        LoginResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertNull(response.getToken());
        assertEquals("Email already exists", response.getMessage());

        verify(userRepository, times(1)).findByUsername("newuser");
        verify(userRepository, times(1)).findByEmail("jane.smith@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should register user with null email")
    void testRegister_WithNullEmail() {
        registerRequest.setEmail(null);
        User newUser = new User("newuser", "encodedNewPassword123", "Jane", "Smith", null);
        newUser.setId(2L);

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpassword123")).thenReturn("encodedNewPassword123");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(jwtService.generateToken("newuser", 2L)).thenReturn("jwt-token-789");

        LoginResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token-789", response.getToken());
        assertEquals(2L, response.getId());
        assertEquals("newuser", response.getUsername());

        verify(userRepository, times(1)).findByUsername("newuser");
        verify(userRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, times(1)).encode("newpassword123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle authentication exception gracefully")
    void testAuthenticate_Exception() {
        when(userRepository.findByUsername("testuser")).thenThrow(new RuntimeException("Database error"));

        LoginResponse response = authService.authenticate(loginRequest);

        assertNotNull(response);
        assertNull(response.getToken());
        assertTrue(response.getMessage().contains("Authentication failed"));
        assertTrue(response.getMessage().contains("Database error"));

        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should handle registration exception gracefully")
    void testRegister_Exception() {
        when(userRepository.findByUsername("newuser")).thenThrow(new RuntimeException("Database error"));

        LoginResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertNull(response.getToken());
        assertTrue(response.getMessage().contains("Registration failed"));
        assertTrue(response.getMessage().contains("Database error"));

        verify(userRepository, times(1)).findByUsername("newuser");
    }

    @Test
    @DisplayName("Should update last login timestamp on successful authentication")
    void testAuthenticate_UpdatesLastLogin() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encodedPassword123")).thenReturn(true);
        when(jwtService.generateToken("testuser", 1L)).thenReturn("jwt-token-123");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        authService.authenticate(loginRequest);

        assertNotNull(testUser.getLastLogin());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should set default role and enabled status on registration")
    void testRegister_SetsDefaultValues() {
        User newUser = new User("newuser", "encodedNewPassword123", "Jane", "Smith", "jane.smith@example.com");
        newUser.setId(2L);

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("jane.smith@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpassword123")).thenReturn("encodedNewPassword123");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertEquals("USER", savedUser.getRole());
            assertTrue(savedUser.isEnabled());
            assertNotNull(savedUser.getCreatedAt());
            assertNotNull(savedUser.getUpdatedAt());
            return newUser;
        });
        when(jwtService.generateToken("newuser", 2L)).thenReturn("jwt-token-456");

        authService.register(registerRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }
}
