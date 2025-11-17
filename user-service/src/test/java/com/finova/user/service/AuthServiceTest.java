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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService
 * Tests authentication and registration flows with security considerations
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
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setEnabled(true);
        testUser.setRole("USER");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Jane");
        registerRequest.setLastName("Smith");
        registerRequest.setEmail("jane.smith@example.com");
    }

    @Test
    @DisplayName("Should authenticate user successfully with valid credentials")
    void testAuthenticateSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken("testuser", 1L)).thenReturn("mock-jwt-token");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        LoginResponse response = authService.authenticate(loginRequest);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals("Login successful", response.getMessage());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", testUser.getPassword());
        verify(jwtService, times(1)).generateToken("testuser", 1L);
        verify(userRepository, times(1)).save(any(User.class));
        assertNotNull(testUser.getLastLogin());
    }

    @Test
    @DisplayName("Should fail authentication with invalid username")
    void testAuthenticateInvalidUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        LoginResponse response = authService.authenticate(loginRequest);

        assertNotNull(response);
        assertNull(response.getToken());
        assertEquals("Invalid username or password", response.getMessage());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("Should fail authentication with invalid password")
    void testAuthenticateInvalidPassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(false);

        LoginResponse response = authService.authenticate(loginRequest);

        assertNotNull(response);
        assertNull(response.getToken());
        assertEquals("Invalid username or password", response.getMessage());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", testUser.getPassword());
        verify(jwtService, never()).generateToken(anyString(), anyLong());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail authentication when user account is disabled")
    void testAuthenticateDisabledAccount() {
        testUser.setEnabled(false);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);

        LoginResponse response = authService.authenticate(loginRequest);

        assertNotNull(response);
        assertNull(response.getToken());
        assertEquals("Account is disabled", response.getMessage());

        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", testUser.getPassword());
        verify(jwtService, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("Should update last login timestamp on successful authentication")
    void testAuthenticateUpdatesLastLogin() {
        LocalDateTime beforeLogin = LocalDateTime.now();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(jwtService.generateToken("testuser", 1L)).thenReturn("mock-jwt-token");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        authService.authenticate(loginRequest);

        assertNotNull(testUser.getLastLogin());
        assertTrue(testUser.getLastLogin().isAfter(beforeLogin) || 
                   testUser.getLastLogin().isEqual(beforeLogin));
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    @DisplayName("Should handle authentication exception gracefully")
    void testAuthenticateException() {
        when(userRepository.findByUsername(anyString())).thenThrow(new RuntimeException("Database error"));

        LoginResponse response = authService.authenticate(loginRequest);

        assertNotNull(response);
        assertNull(response.getToken());
        assertTrue(response.getMessage().contains("Authentication failed"));
        assertTrue(response.getMessage().contains("Database error"));
    }

    @Test
    @DisplayName("Should register new user successfully")
    void testRegisterSuccess() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("jane.smith@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(jwtService.generateToken("newuser", 1L)).thenReturn("mock-jwt-token");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setPassword("$2a$10$encodedPassword");
        savedUser.setFirstName("Jane");
        savedUser.setLastName("Smith");
        savedUser.setEmail("jane.smith@example.com");
        savedUser.setEnabled(true);
        savedUser.setRole("USER");
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        LoginResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals(1L, response.getId());
        assertEquals("newuser", response.getUsername());
        assertEquals("jane.smith@example.com", response.getEmail());
        assertEquals("Login successful", response.getMessage());

        verify(userRepository, times(1)).findByUsername("newuser");
        verify(userRepository, times(1)).findByEmail("jane.smith@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken("newuser", 1L);
    }

    @Test
    @DisplayName("Should fail registration when username already exists")
    void testRegisterUsernameExists() {
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
    void testRegisterEmailExists() {
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
    void testRegisterWithNullEmail() {
        registerRequest.setEmail(null);
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(jwtService.generateToken("newuser", 1L)).thenReturn("mock-jwt-token");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setPassword("$2a$10$encodedPassword");
        savedUser.setFirstName("Jane");
        savedUser.setLastName("Smith");
        savedUser.setEmail(null);
        savedUser.setEnabled(true);
        savedUser.setRole("USER");
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        LoginResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertNull(response.getEmail());

        verify(userRepository, times(1)).findByUsername("newuser");
        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should set default values when registering new user")
    void testRegisterSetsDefaultValues() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("jane.smith@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(jwtService.generateToken(anyString(), anyLong())).thenReturn("mock-jwt-token");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setEnabled(true);
        savedUser.setRole("USER");
        
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertTrue(user.isEnabled());
            assertEquals("USER", user.getRole());
            assertNotNull(user.getCreatedAt());
            assertNotNull(user.getUpdatedAt());
            return savedUser;
        });

        authService.register(registerRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should encode password when registering new user")
    void testRegisterEncodesPassword() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("jane.smith@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(jwtService.generateToken(anyString(), anyLong())).thenReturn("mock-jwt-token");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            assertEquals("$2a$10$encodedPassword", user.getPassword());
            return savedUser;
        });

        authService.register(registerRequest);

        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle registration exception gracefully")
    void testRegisterException() {
        when(userRepository.findByUsername(anyString())).thenThrow(new RuntimeException("Database error"));

        LoginResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertNull(response.getToken());
        assertTrue(response.getMessage().contains("Registration failed"));
        assertTrue(response.getMessage().contains("Database error"));
    }

    @Test
    @DisplayName("Should generate JWT token on successful registration")
    void testRegisterGeneratesJwtToken() {
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("jane.smith@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(jwtService.generateToken("newuser", 1L)).thenReturn("generated-jwt-token");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setEmail("jane.smith@example.com");
        
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        LoginResponse response = authService.register(registerRequest);

        assertEquals("generated-jwt-token", response.getToken());
        verify(jwtService, times(1)).generateToken("newuser", 1L);
    }
}
